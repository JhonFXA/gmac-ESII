package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.PaginaPacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.pacientesServicos.ServicoListarPaciente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoListarPacienteTest {

    @Mock
    private RepositorioPaciente repositorioPaciente;

    @InjectMocks
    private ServicoListarPaciente servico;

    private Paciente criarPaciente() {
        Paciente paciente = new Paciente();
        paciente.setNome("João");
        paciente.setCpf("12345678901"); // salvo sem formatação
        paciente.setStatusSolicitacao(StatusSolicitacao.PENDENTE);
        paciente.setDataNascimento(LocalDate.of(1990, 1, 1));
        return paciente;
    }

    @Test
    void deveListarPacientesComOrdenacaoCrescente() {

        Paciente paciente = criarPaciente();

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(Sort.class);

        when(repositorioPaciente.findAll(
                any(Specification.class),
                any(Sort.class))
        ).thenReturn(List.of(paciente));

        List<PaginaPacienteDTO> resultado =
                servico.listarPacientes(null, null, null, false);

        verify(repositorioPaciente)
                .findAll(any(Specification.class), sortCaptor.capture());

        Sort sortUsado = sortCaptor.getValue();

        assertEquals(Sort.Direction.ASC,
                sortUsado.getOrderFor("nome").getDirection());

        assertEquals(1, resultado.size());
    }

    @Test
    void deveListarPacientesComOrdenacaoDecrescente() {

        Paciente paciente = criarPaciente();

        ArgumentCaptor<Sort> sortCaptor =
                ArgumentCaptor.forClass(Sort.class);

        when(repositorioPaciente.findAll(
                any(Specification.class),
                any(Sort.class))
        ).thenReturn(List.of(paciente));

        servico.listarPacientes(null, null, null, true);

        verify(repositorioPaciente)
                .findAll(any(Specification.class), sortCaptor.capture());

        Sort sortUsado = sortCaptor.getValue();

        assertEquals(Sort.Direction.DESC,
                sortUsado.getOrderFor("nome").getDirection());
    }

    @Test
    void deveFormatarCpfNoRetornoDoDTO() {

        Paciente paciente = criarPaciente();

        when(repositorioPaciente.findAll(
                any(Specification.class),
                any(Sort.class))
        ).thenReturn(List.of(paciente));

        List<PaginaPacienteDTO> resultado =
                servico.listarPacientes(null, null, null, false);

        PaginaPacienteDTO dto = resultado.get(0);

        assertEquals("123.456.789-01", dto.cpf());
        assertEquals("João", dto.nome());
        assertEquals(StatusSolicitacao.PENDENTE, dto.statusSolicitacao());
        assertEquals(LocalDate.of(1990, 1, 1), dto.dataNascimento());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirPaciente() {

        when(repositorioPaciente.findAll(
                any(Specification.class),
                any(Sort.class))
        ).thenReturn(List.of());

        List<PaginaPacienteDTO> resultado =
                servico.listarPacientes(null, null, null, false);

        assertTrue(resultado.isEmpty());
    }
}
