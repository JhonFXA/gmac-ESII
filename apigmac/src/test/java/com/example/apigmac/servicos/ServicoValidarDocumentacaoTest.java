package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.entidades.*;
import com.example.apigmac.modelo.enums.*;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioValidacaoDocumentacao;
import com.example.apigmac.servicos.documentacaoServicos.ServicoValidarDocumentacao;
import com.example.apigmac.servicos.emailServicos.ServicoEmail;
import com.example.apigmac.servicos.periciaServicos.ServicoMarcarPericia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ServicoValidarDocumentacaoTest {

    @Mock private ServicoEmail servicoEmail;
    @Mock private RepositorioDocumentacao repositorioDocumentacao;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private RepositorioValidacaoDocumentacao repositorioValidacaoDocumentacao;
    @Mock private ServicoMarcarPericia servicoMarcarPericia;

    @InjectMocks
    private ServicoValidarDocumentacao servico;

    private Usuario medico;
    private Usuario atendente;
    private Paciente paciente;
    private Documentacao documentacao;
    private UUID docId;

    @BeforeEach
    void setup() {

        medico = new Usuario();
        medico.setPerfil(Perfil.MEDICO);

        atendente = new Usuario();
        atendente.setPerfil(Perfil.RECEPCIONISTA);

        paciente = new Paciente();
        paciente.setNome("João");
        paciente.setEmail("teste@email.com");
        paciente.setStatusSolicitacao(StatusSolicitacao.PENDENTE);

        documentacao = new Documentacao();
        documentacao.setPaciente(paciente);
        documentacao.setStatusDocumentacao(StatusDocumentacao.PENDENTE);

        paciente.setDocumentacoes(List.of(documentacao));

        docId = UUID.randomUUID();
    }

    private void mockAuth(Usuario usuario) {
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(usuario);
    }

    @Test
    void deveAprovarDocumento() {

        mockAuth(medico);

        when(repositorioDocumentacao.findById(docId))
                .thenReturn(Optional.of(documentacao));

        ValidacaoDocumentacaoDTO dto =
                new ValidacaoDocumentacaoDTO("login","senha",
                                        docId,
                        "ok",
                        StatusValidacaoDocumentacao.APROVADA,
                                        null);

        servico.registrarValidacao(dto);

        assertEquals(StatusDocumentacao.APROVADA,
                documentacao.getStatusDocumentacao());

        verify(servicoEmail).enviarEmailTexto(any(), any(), any());
        verify(repositorioValidacaoDocumentacao).save(any());
        verify(repositorioDocumentacao).save(documentacao);
    }

    @Test
    void deveReprovarDocumento() {

        mockAuth(medico);

        when(repositorioDocumentacao.findById(docId))
                .thenReturn(Optional.of(documentacao));

        ValidacaoDocumentacaoDTO dto =
                new ValidacaoDocumentacaoDTO("login","senha",
                                        docId,
                        "erro",
                        StatusValidacaoDocumentacao.REPROVADA,
                                        null);

        servico.registrarValidacao(dto);

        assertEquals(StatusDocumentacao.REPROVADA,
                documentacao.getStatusDocumentacao());

        verify(servicoEmail).enviarEmailTexto(any(), any(), any());
    }

    @Test
    void deveSolicitarPericia() {

        mockAuth(atendente);

        when(repositorioDocumentacao.findById(docId))
                .thenReturn(Optional.of(documentacao));

        LocalDateTime data = LocalDateTime.now();

        ValidacaoDocumentacaoDTO dto =
                new ValidacaoDocumentacaoDTO("login","senha",
                                        docId,
                        "necessário",
                        StatusValidacaoDocumentacao.PERICIA,
                                        data);

        servico.registrarValidacao(dto);

        assertEquals(StatusDocumentacao.PERICIA,
                documentacao.getStatusDocumentacao());

        verify(servicoMarcarPericia).marcarPericia(any());
        verify(servicoEmail).enviarEmailTexto(any(), any(), any());
    }

    @Test
    void deveLancarErroSePericiaSemData() {

        mockAuth(atendente);

        when(repositorioDocumentacao.findById(docId))
                .thenReturn(Optional.of(documentacao));

        ValidacaoDocumentacaoDTO dto =
                new ValidacaoDocumentacaoDTO("login","senha",
                                        docId,
                        "necessário",
                        StatusValidacaoDocumentacao.PERICIA,
                                        null);

        assertThrows(IllegalArgumentException.class,
                () -> servico.registrarValidacao(dto));
    }

    @Test
    void deveNegarPermissaoSeNaoForMedico() {

        mockAuth(atendente);

        when(repositorioDocumentacao.findById(docId))
                .thenReturn(Optional.of(documentacao));

        ValidacaoDocumentacaoDTO dto =
                new ValidacaoDocumentacaoDTO("login","senha",
                                        docId,
                        "ok",
                        StatusValidacaoDocumentacao.APROVADA,
                                        null);

        assertThrows(AccessDeniedException.class,
                () -> servico.registrarValidacao(dto));
    }

    @Test
    void deveLancarErroSeDocumentoNaoExistir() {

        mockAuth(medico);

        when(repositorioDocumentacao.findById(docId))
                .thenReturn(Optional.empty());

        ValidacaoDocumentacaoDTO dto =
                new ValidacaoDocumentacaoDTO("login","senha",
                                        docId,
                        "ok",
                        StatusValidacaoDocumentacao.APROVADA,
                                        null);

        assertThrows(NoSuchElementException.class,
                () -> servico.registrarValidacao(dto));
    }

    @Test
    void deveLancarErroSeCredenciaisInvalidas() {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new AuthenticationException("erro") {});

        ValidacaoDocumentacaoDTO dto =
                new ValidacaoDocumentacaoDTO("login","senha",
                                        docId,
                        "ok",
                        StatusValidacaoDocumentacao.APROVADA,
                                        null);

        assertThrows(IllegalArgumentException.class,
                () -> servico.registrarValidacao(dto));
    }
}
