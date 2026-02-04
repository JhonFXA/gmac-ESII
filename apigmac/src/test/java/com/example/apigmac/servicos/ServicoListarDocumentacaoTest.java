package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.DocumentoDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.servicos.documentacaoServicos.ServicoListarDocumentacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoListarDocumentacaoTest {

    @Mock
    private RepositorioDocumentacao repositorioDocumentacao;

    @InjectMocks
    private ServicoListarDocumentacao servico;

    private Documentacao documentacao;
    private Paciente paciente;

    @BeforeEach
    void setUp() {
        paciente = new Paciente();
        paciente.setCpf("12345678901");
        paciente.setNome("João");

        documentacao = new Documentacao();
        documentacao.setId(UUID.randomUUID());
        documentacao.setPaciente(paciente);
        documentacao.setDataEnvio(LocalDate.now());
        documentacao.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
    }

    @Test
    void deveListarDocumentosComStatusDefaultPendente() {

        when(repositorioDocumentacao.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(documentacao));

        List<DocumentoDTO> resultado =
                servico.listarDocumentos(null, null, null, false);

        assertEquals(1, resultado.size());
        assertEquals("João", resultado.get(0).nome());
        assertEquals(StatusDocumentacao.PENDENTE.name(), resultado.get(0).status());
    }

    @Test
    void deveOrdenarPorDataEnvioCrescente() {

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        when(repositorioDocumentacao.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(documentacao));

        servico.listarDocumentos(null, null, StatusDocumentacao.PENDENTE, false);

        verify(repositorioDocumentacao)
                .findAll(any(Specification.class), sortCaptor.capture());

        Sort sortUsado = sortCaptor.getValue();

        assertEquals(Sort.Direction.ASC,
                sortUsado.getOrderFor("dataEnvio").getDirection());
    }

    @Test
    void deveOrdenarPorDataEnvioDecrescente() {

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);

        when(repositorioDocumentacao.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(documentacao));

        servico.listarDocumentos(null, null, StatusDocumentacao.PENDENTE, true);

        verify(repositorioDocumentacao)
                .findAll(any(Specification.class), sortCaptor.capture());

        Sort sortUsado = sortCaptor.getValue();

        assertEquals(Sort.Direction.DESC,
                sortUsado.getOrderFor("dataEnvio").getDirection());
    }

    @Test
    void deveFormatarCpfNoRetornoDoDTO() {

        when(repositorioDocumentacao.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of(documentacao));

        List<DocumentoDTO> resultado =
                servico.listarDocumentos(
                        "123.456.789-01",
                        null,
                        StatusDocumentacao.PENDENTE,
                        false
                );

        DocumentoDTO dto = resultado.get(0);

        assertEquals("123.456.789-01", dto.cpf());
        assertEquals("João", dto.nome());
        assertEquals(StatusDocumentacao.PENDENTE.name(), dto.status());
        assertNotNull(dto.id());
        assertNotNull(dto.dataEnvio());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverResultados() {

        when(repositorioDocumentacao.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(List.of());

        List<DocumentoDTO> resultado =
                servico.listarDocumentos(
                        null,
                        null,
                        StatusDocumentacao.PENDENTE,
                        false
                );

        assertTrue(resultado.isEmpty());
    }
}
