package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.Sexo;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioEndereco;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.documentacaoServicos.ServicoTransformarDocumentacao;
import com.example.apigmac.servicos.pacientesServicos.ServicoCadastrarPaciente;
import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @Mock
    private MultipartFile documento;

    @InjectMocks
    private ServicoCadastrarPaciente servico;

    private PacienteDTO dtoValido;
    private Paciente pacienteMock;

    @BeforeEach
    void setUp() {

        EnderecoDTO endereco = new EnderecoDTO(
                "49000-000",
                "Aracaju",
                "SE",
                "Centro",
                "Rua A",
                "123",
                "Apto 1"
        );

        dtoValido = new PacienteDTO(
                "João Silva",
                "123.456.789-01",
                null,
                LocalDate.of(2000, 1, 1),
                "(79) 90000-0000",
                "joao@email.com",
                Sexo.MASCULINO,
                EstadoCivil.SOLTEIRO,
                List.of(endereco)
        );

        pacienteMock = new Paciente();
        pacienteMock.setCpf("12345678901");
    }

    @Test
    void deveCadastrarPacienteComSucesso() {

        when(verificacao.textoObrigatorioValido(anyString(), anyInt())).thenReturn(true);
        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(verificacao.emailValido(anyString())).thenReturn(true);
        when(verificacao.telefoneValido(anyString())).thenReturn(true);
        when(verificacao.dataNascimentoValida(any())).thenReturn(true);
        when(verificacao.cepValido(anyString())).thenReturn(true);
        when(verificacao.estadoValido(anyString())).thenReturn(true);
        when(verificacao.pdfValido(any())).thenReturn(true);

        when(repositorioPaciente.findByCpf(anyString())).thenReturn(null, pacienteMock);

        when(repositorioPaciente.findByEmail(anyString())).thenReturn(null);
        when(repositorioPaciente.save(any())).thenReturn(pacienteMock);

        when(documento.isEmpty()).thenReturn(false);

        Paciente paciente = servico.cadastrarPaciente(dtoValido, documento);

        assertNotNull(paciente);
        assertEquals("12345678901", paciente.getCpf());

        verify(repositorioPaciente).save(any(Paciente.class));
        verify(repositorioEndereco).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoCpfInvalido() {

        when(verificacao.textoObrigatorioValido(anyString(), anyInt())).thenReturn(true);
        when(verificacao.cpfValido(anyString())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.cadastrarPaciente(dtoValido, documento)
        );

        assertEquals("CPF inválido", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {

        when(verificacao.textoObrigatorioValido(anyString(), anyInt())).thenReturn(true);
        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(repositorioPaciente.findByCpf(anyString())).thenReturn(new Paciente());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.cadastrarPaciente(dtoValido, documento)
        );

        assertEquals("Paciente já cadastrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNaoInformarEndereco() {

        PacienteDTO dtoSemEndereco = new PacienteDTO(
                "João Silva",
                "123.456.789-01",
                null,
                LocalDate.of(2000, 1, 1),
                "(79) 90000-0000",
                "email@email.com",
                Sexo.MASCULINO,
                EstadoCivil.SOLTEIRO,
                null
        );

        when(verificacao.textoObrigatorioValido(anyString(), anyInt())).thenReturn(true);
        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(verificacao.emailValido(anyString())).thenReturn(true);
        when(verificacao.telefoneValido(anyString())).thenReturn(true);
        when(verificacao.dataNascimentoValida(any())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.cadastrarPaciente(dtoSemEndereco, documento)
        );

        assertEquals("É obrigatório informar pelo menos um endereço", ex.getMessage());
    }
}
