package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.servicos.documentacaoServicos.ServicoValidarDocumentacao;
import com.example.apigmac.servicos.periciaServicos.ServicoRealizarPericia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoRealizarPericiaTest {

    @Mock
    private ServicoValidarDocumentacao servicoValidarDocumentacao;

    @Mock
    private RepositorioDocumentacao repositorioDocumentacao;

    @Mock
    private RepositorioPericia repositorioPericia;

    @InjectMocks
    private ServicoRealizarPericia servico;

    private UUID periciaId;
    private Pericia pericia;
    private ValidacaoDocumentacaoDTO dto;

    @BeforeEach
    void setup() {
        periciaId = UUID.randomUUID();
        pericia = new Pericia();
        pericia.setId(periciaId);
        pericia.setStatusPericia(StatusPericia.AGENDADA);

        dto = mock(ValidacaoDocumentacaoDTO.class);
    }

    @Test
    void deveLancarExcecaoQuandoPericiaNaoEncontrada() {
        when(repositorioPericia.findById(periciaId))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> servico.validarPericia(dto, periciaId));

        verify(repositorioPericia, never()).save(any());
        verify(servicoValidarDocumentacao, never()).registrarValidacao(any());
    }

    @Test
    void deveFinalizarPericiaERegistrarValidacao() {
        when(repositorioPericia.findById(periciaId))
                .thenReturn(Optional.of(pericia));

        servico.validarPericia(dto, periciaId);

        assertEquals(StatusPericia.FINALIZADA, pericia.getStatusPericia());

        verify(servicoValidarDocumentacao, times(1))
                .registrarValidacao(dto);

        verify(repositorioPericia, times(1))
                .save(pericia);
    }
}
