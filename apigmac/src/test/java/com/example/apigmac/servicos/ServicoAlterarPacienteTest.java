package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarPacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.pacientesServicos.ServicoAlterarPaciente;
import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ServicoAlterarPacienteTest {

    @Mock
    private RepositorioPaciente repositorioPaciente;

    @Mock
    private ServicoVerificacao verificacao;

    @InjectMocks
    private ServicoAlterarPaciente servico;

    private Paciente pacienteExistente;
    private final UUID ID_PACIENTE = UUID.randomUUID();
    private final String CPF_TESTE = "123.456.789-01";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Criando o paciente que já existe no sistema
        pacienteExistente = new Paciente();
        pacienteExistente.setId(ID_PACIENTE); // ID UUID
        pacienteExistente.setNome("Nome Antigo");
        pacienteExistente.setCpf(CPF_TESTE);
        pacienteExistente.setEmail("antigo@email.com");
        pacienteExistente.setTelefone("(79) 90000-0000");
    }

    @Test
    void deveAlterarNomeETelefoneComSucesso() {
        // Arrange
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_TESTE, "Novo Nome", "(79) 91111-1111", null, null, null, null, null
        );

        when(repositorioPaciente.findByCpf(CPF_TESTE)).thenReturn(pacienteExistente);
        when(verificacao.textoObrigatorioValido(anyString(), anyInt())).thenReturn(true);
        when(verificacao.telefoneValido(anyString())).thenReturn(true);

        // Act
        servico.alterarPaciente(dto);

        // Assert
        assertEquals("Novo Nome", pacienteExistente.getNome());
        assertEquals("(79) 91111-1111", pacienteExistente.getTelefone());
        // Garante que o email não foi tocado
        assertEquals("antigo@email.com", pacienteExistente.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastradoPorOutroPaciente() {
        // Arrange
        String novoEmail = "outro@email.com";
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_TESTE, null, null, novoEmail, null, null, null, null
        );

        Paciente outroPaciente = new Paciente();
        outroPaciente.setId(UUID.randomUUID()); // ID diferente do ID_PACIENTE
        outroPaciente.setEmail(novoEmail);

        when(repositorioPaciente.findByCpf(CPF_TESTE)).thenReturn(pacienteExistente);
        when(verificacao.emailValido(novoEmail)).thenReturn(true);
        when(repositorioPaciente.findByEmail(novoEmail)).thenReturn(outroPaciente);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                servico.alterarPaciente(dto)
        );
        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    void devePermitirAlterarEmailSeForOMesmoEmailDoProprioPaciente() {
        // Arrange
        String mesmoEmail = "antigo@email.com";
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_TESTE, null, null, mesmoEmail, null, null, null, null
        );

        when(repositorioPaciente.findByCpf(CPF_TESTE)).thenReturn(pacienteExistente);

        // Act
        servico.alterarPaciente(dto);

        // Assert
        // O método não deve chamar a verificação nem o repositório se o e-mail for idêntico
        verify(verificacao, never()).emailValido(anyString());
        assertEquals(mesmoEmail, pacienteExistente.getEmail());
    }

    @Test
    void deveLancarExcecaoParaDataNascimentoFutura() {
        // Arrange
        LocalDate dataInvalida = LocalDate.now().plusDays(1);
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_TESTE, null, null, null, null, null, null, dataInvalida
        );

        when(repositorioPaciente.findByCpf(CPF_TESTE)).thenReturn(pacienteExistente);
        when(verificacao.dataNascimentoValida(dataInvalida)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> servico.alterarPaciente(dto));
    }

    @Test
    void deveRejeitarNomeCurtoDemais() {
        // Arrange
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_TESTE, "Ab", null, null, null, null, null, null
        );

        when(repositorioPaciente.findByCpf(CPF_TESTE)).thenReturn(pacienteExistente);
        when(verificacao.textoObrigatorioValido("Ab", 3)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> servico.alterarPaciente(dto));
    }
}