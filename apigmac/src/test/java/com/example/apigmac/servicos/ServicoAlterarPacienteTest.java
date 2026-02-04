package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarPacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.pacientesServicos.ServicoAlterarPaciente;
import com.example.apigmac.utils.ServicoVerificacao;
import com.example.apigmac.utils.CpfUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoAlterarPacienteTest {

    @Mock
    private RepositorioPaciente repositorioPaciente;

    @Mock
    private ServicoVerificacao verificacao;

    @InjectMocks
    private ServicoAlterarPaciente servico;

    private Paciente pacienteExistente;

    private final UUID ID_PACIENTE = UUID.randomUUID();
    private final String CPF_COM_MASCARA = "123.456.789-01";
    private final String CPF_NORMALIZADO = "12345678901";

    @BeforeEach
    void setUp() {
        pacienteExistente = new Paciente();
        pacienteExistente.setId(ID_PACIENTE);
        pacienteExistente.setNome("Nome Antigo");
        pacienteExistente.setCpf(CPF_NORMALIZADO);
        pacienteExistente.setEmail("antigo@email.com");
        pacienteExistente.setTelefone("(79) 90000-0000");
    }

    @Test
    void deveAlterarNomeETelefoneComSucesso() {
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_COM_MASCARA, "Novo Nome", "(79) 91111-1111",
                null, null, null, null, null
        );

        when(repositorioPaciente.findByCpf(CPF_NORMALIZADO)).thenReturn(pacienteExistente);
        when(verificacao.cpfValido(CPF_COM_MASCARA)).thenReturn(true); // <- adiciona
        when(verificacao.textoObrigatorioValido("Novo Nome", 3)).thenReturn(true);
        when(verificacao.telefoneValido("(79) 91111-1111")).thenReturn(true);

        servico.alterarPaciente(dto, CPF_COM_MASCARA);

        assertEquals("Novo Nome", pacienteExistente.getNome());
        assertEquals("(79) 91111-1111", pacienteExistente.getTelefone());
        assertEquals("antigo@email.com", pacienteExistente.getEmail());
    }


    @Test
    void deveLancarExcecaoQuandoEmailJaCadastradoPorOutroPaciente() {
        String novoEmail = "outro@email.com";
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_COM_MASCARA, null, null,
                novoEmail, null, null, null, null
        );

        Paciente outroPaciente = new Paciente();
        outroPaciente.setId(UUID.randomUUID());
        outroPaciente.setEmail(novoEmail);

        when(repositorioPaciente.findByCpf(CPF_NORMALIZADO)).thenReturn(pacienteExistente);
        when(verificacao.cpfValido(CPF_COM_MASCARA)).thenReturn(true); // <- Adicionado
        when(verificacao.emailValido(novoEmail)).thenReturn(true);
        when(repositorioPaciente.findByEmail(novoEmail)).thenReturn(outroPaciente);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.alterarPaciente(dto, CPF_COM_MASCARA)
        );

        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    void devePermitirAlterarEmailSeForOMesmoEmailDoProprioPaciente() {
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_COM_MASCARA, null, null,
                "antigo@email.com", null, null, null, null
        );

        when(repositorioPaciente.findByCpf(CPF_NORMALIZADO)).thenReturn(pacienteExistente);
        when(verificacao.cpfValido(CPF_COM_MASCARA)).thenReturn(true); // <- Adicionado

        servico.alterarPaciente(dto, CPF_COM_MASCARA);

        // Email não precisa ser validado novamente se for o mesmo do paciente
        verify(verificacao, never()).emailValido(anyString());
        assertEquals("antigo@email.com", pacienteExistente.getEmail());
    }


    @Test
    void deveLancarExcecaoParaDataNascimentoFutura() {
        LocalDate dataInvalida = LocalDate.now().plusDays(1);
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_COM_MASCARA, null, null,
                null, null, null, null, dataInvalida
        );

        when(repositorioPaciente.findByCpf(CPF_NORMALIZADO)).thenReturn(pacienteExistente);
        when(verificacao.cpfValido(CPF_COM_MASCARA)).thenReturn(true); // <- Adicionado
        when(verificacao.dataNascimentoValida(dataInvalida)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.alterarPaciente(dto, CPF_COM_MASCARA)
        );

        assertEquals("Data de nascimento inválida", ex.getMessage());
    }

    @Test
    void deveRejeitarNomeCurtoDemais() {
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                CPF_COM_MASCARA, "Ab", null,
                null, null, null, null, null
        );

        when(repositorioPaciente.findByCpf(CPF_NORMALIZADO)).thenReturn(pacienteExistente);
        when(verificacao.cpfValido(CPF_COM_MASCARA)).thenReturn(true); // <- Adicionado
        when(verificacao.textoObrigatorioValido("Ab", 3)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.alterarPaciente(dto, CPF_COM_MASCARA)
        );

        assertEquals("Nome inválido", ex.getMessage());
    }


    @Test
    void deveAlterarCPFComSucesso() {
        String novoCpf = "987.654.321-00";
        AlterarPacienteDTO dto = new AlterarPacienteDTO(
                novoCpf, null, null,
                null, null, null, null, null
        );

        when(repositorioPaciente.findByCpf(CPF_NORMALIZADO)).thenReturn(pacienteExistente);
        when(verificacao.cpfValido(novoCpf)).thenReturn(true);

        servico.alterarPaciente(dto, CPF_COM_MASCARA);

        assertEquals(CpfUtils.normalizar(novoCpf), pacienteExistente.getCpf());
    }
}
