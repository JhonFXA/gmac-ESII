package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.RelatorioBeneficioDTO;
import com.example.apigmac.DTOs.RelatorioDocumentacaoDTO;
import com.example.apigmac.modelo.enums.TipoPeriodo;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.servicos.relatorioServicos.ServicoGerarRelatorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoGerarRelatorioTest {

    @Mock
    private RepositorioDocumentacao repositorioDocumentacao;

    @Mock
    private RepositorioPaciente repositorioPaciente;

    @InjectMocks
    private ServicoGerarRelatorio servico;

    @BeforeEach
    void setup() {
        // Nada necessário aqui
    }

    @Test
    void deveGerarRelatorioDocumentacaoCorretamente() {

        when(repositorioDocumentacao.count(any(Specification.class)))
                .thenReturn(5L)   // pendente
                .thenReturn(10L)  // aprovada
                .thenReturn(3L)   // reprovada
                .thenReturn(18L); // total

        RelatorioDocumentacaoDTO dto =
                servico.gerarRelatorioDocumentacao(2025, TipoPeriodo.ANO, 1);

        assertNotNull(dto);
        assertEquals(18L, dto.totalDocumentacoes());
        assertEquals(10L, dto.aprovadas());
        assertEquals(3L, dto.reprovadas());
        assertEquals(5L, dto.pendentes());

        verify(repositorioDocumentacao, times(4))
                .count(any(Specification.class));
    }

    @Test
    void deveGerarRelatorioBeneficioCorretamente() {

        when(repositorioPaciente.count(any(Specification.class)))
                .thenReturn(7L)   // beneficiados
                .thenReturn(4L)   // nao beneficiados
                .thenReturn(2L);  // pendentes

        when(repositorioPaciente.count())
                .thenReturn(13L); // total

        RelatorioBeneficioDTO dto =
                servico.gerarRelatorioBeneficio(2025, TipoPeriodo.ANO, 1);

        assertNotNull(dto);
        assertEquals(13L, dto.totalPacientes());
        assertEquals(7L, dto.pacientesBeneficiados());
        assertEquals(4L, dto.pacientesNaoBeneficiados());
        assertEquals(2L, dto.pacientesPendentes());

        verify(repositorioPaciente, times(3))
                .count(any(Specification.class));

        verify(repositorioPaciente, times(1))
                .count();
    }
}
