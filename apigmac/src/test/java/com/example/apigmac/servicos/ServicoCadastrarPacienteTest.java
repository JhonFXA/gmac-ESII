package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.Sexo;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioEndereco;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.documentacaoServicos.ServicoTransformarDocumentacao;
import com.example.apigmac.servicos.pacientesServicos.ServicoCadastrarPaciente;
import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServicoCadastrarPacienteTest {

    @Mock
    private RepositorioPaciente repositorioPaciente;
    @Mock
    private RepositorioDocumentacao repositorioDocumentacao;
    @Mock
    private RepositorioEndereco repositorioEndereco;
    @Mock
    private ServicoVerificacao verificacao;
    @Mock
    private ServicoTransformarDocumentacao transformarDocumentacao;

    @InjectMocks
    private ServicoCadastrarPaciente servico;

    private PacienteDTO pacienteDTO;
    private MockMultipartFile documentoMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock de um EnderecoDTO
        EnderecoDTO enderecoDTO = new EnderecoDTO("49000-000", "Aracaju", "SE", "Centro", "Rua A", "123", "");

        // Mock do PacienteDTO
        pacienteDTO = new PacienteDTO(
                "João Silva",
                "123.456.789-01",
                StatusSolicitacao.PENDENTE,
                LocalDate.of(1990, 1, 1),
                "(79) 99999-9999",
                "joao@email.com",
                Sexo.MASCULINO,
                EstadoCivil.SOLTEIRO,
                List.of(enderecoDTO)
        );

        documentoMock = new MockMultipartFile("documento", "teste.pdf", "application/pdf", "conteudo".getBytes());
    }

    @Test
    void deveCadastrarPacienteComSucesso() {
        // 1. Mocks de validação para retornarem sempre true
        when(verificacao.textoObrigatorioValido(anyString(), anyInt())).thenReturn(true);
        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(verificacao.emailValido(anyString())).thenReturn(true);
        when(verificacao.telefoneValido(anyString())).thenReturn(true);
        when(verificacao.dataNascimentoValida(any())).thenReturn(true);
        when(verificacao.pdfValido(any())).thenReturn(true);
        when(verificacao.cepValido(anyString())).thenReturn(true);
        when(verificacao.estadoValido(anyString())).thenReturn(true);

        // 2. Criamos o objeto paciente usando o construtor da sua entidade
        Paciente pacienteMock = new Paciente(
                pacienteDTO.nome(),
                pacienteDTO.cpf(),
                pacienteDTO.telefone(),
                pacienteDTO.email(),
                pacienteDTO.sexo(),
                pacienteDTO.estadoCivil(),
                pacienteDTO.statusSolicitacao(),
                pacienteDTO.dataNascimento()
        );

        // 3. O PONTO CHAVE: Configurar as múltiplas chamadas do findByCpf
        // 1ª vez (validação): retorna null
        // 2ª vez (cadastrarEndereco): retorna o paciente
        // 3ª vez (cadastrarDocumento): retorna o paciente
        when(repositorioPaciente.findByCpf(pacienteDTO.cpf()))
                .thenReturn(null)      // Para passar pelo "if (repositorioPaciente.findByCpf... != null)"
                .thenReturn(pacienteMock) // Para o cadastrarEndereco
                .thenReturn(pacienteMock); // Para o cadastrarDocumento

        when(repositorioPaciente.findByEmail(anyString())).thenReturn(null);
        when(repositorioPaciente.save(any(Paciente.class))).thenReturn(pacienteMock);
        when(transformarDocumentacao.caminhoDocumentacao(any(), anyString())).thenReturn("documentos/123/teste.pdf");

        // Act
        Paciente resultado = servico.cadastrarPaciente(pacienteDTO, documentoMock);

        // Assert
        assertNotNull(resultado);
        assertEquals(pacienteDTO.cpf(), resultado.getCpf());
        verify(repositorioPaciente, times(1)).save(any(Paciente.class));
        verify(repositorioEndereco, times(1)).save(any());
        verify(repositorioDocumentacao, times(1)).save(any());
    }
    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        // Arrange
        when(verificacao.textoObrigatorioValido(anyString(), anyInt())).thenReturn(true);
        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(repositorioPaciente.findByCpf(pacienteDTO.cpf())).thenReturn(new Paciente());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                servico.cadastrarPaciente(pacienteDTO, documentoMock)
        );
        assertEquals("Usuário já cadastrado", ex.getMessage());
        verify(repositorioPaciente, never()).save(any());
    }

    @Test
    void deveValidarEnderecoCorretamente() {
        // Arrange
        EnderecoDTO enderecoInvalido = new EnderecoDTO("000", "A", "Invalido", "", "", "", "");
        when(verificacao.cepValido(enderecoInvalido.cep())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                servico.cadastrarEndereco(enderecoInvalido, "123.456.789-01")
        );
        assertEquals("CEP inválido", ex.getMessage());
    }

    @Test
    void deveLancarErroAoCadastrarDocumentoSePacienteNaoExistir() {
        // Arrange
        when(verificacao.pdfValido(any())).thenReturn(true);
        when(repositorioPaciente.findByCpf(anyString())).thenReturn(null);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                servico.cadastrarDocumento(documentoMock, "000.000.000-00")
        );
        assertEquals("Paciente não encontrado", ex.getMessage());
    }
}