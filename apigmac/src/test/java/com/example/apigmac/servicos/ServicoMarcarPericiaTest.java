package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.servicos.periciaServicos.ServicoMarcarPericia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ServicoMarcarPericiaTest {

    @Mock
    private RepositorioPericia repositorioPericia;

    @InjectMocks
    private ServicoMarcarPericia servico;

    private PericiaDTO dto;
    private Paciente paciente;
    private Usuario usuario;
    private Documentacao documentacao;

    @BeforeEach
    void setup() {
        paciente = new Paciente();
        paciente.setId(UUID.randomUUID());
        paciente.setNome("João");

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Dr. Silva");

        documentacao = new Documentacao();
        documentacao.setId(UUID.randomUUID());

        dto = new PericiaDTO(
                LocalDateTime.now(),
                StatusPericia.AGENDADA,
                paciente,
                usuario,
                documentacao
        );
    }

    @Test
    void deveLancarExcecaoQuandoDtoForNull() {
        assertThrows(IllegalArgumentException.class,
                () -> servico.marcarPericia(null));
    }

    @Test
    void deveLancarExcecaoQuandoAlgumCampoObrigatorioForNull() {
        PericiaDTO dtoInvalido = new PericiaDTO(
                null,
                StatusPericia.AGENDADA,
                paciente,
                usuario,
                documentacao
        );

        assertThrows(IllegalArgumentException.class,
                () -> servico.marcarPericia(dtoInvalido));
    }

    @Test
    void deveLancarExcecaoQuandoJaExistirPericiaParaDocumentacao() {
        when(repositorioPericia.existsByDocumentacaoId(documentacao.getId()))
                .thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> servico.marcarPericia(dto));

        verify(repositorioPericia, never()).save(any());
    }

    @Test
    void deveSalvarPericiaQuandoDadosValidos() {
        when(repositorioPericia.existsByDocumentacaoId(documentacao.getId()))
                .thenReturn(false);

        servico.marcarPericia(dto);

        verify(repositorioPericia, times(1)).save(any(Pericia.class));
    }
}
