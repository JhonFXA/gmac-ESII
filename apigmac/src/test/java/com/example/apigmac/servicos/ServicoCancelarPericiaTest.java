package com.example.apigmac.servicos;

import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.servicos.emailServicos.ServicoEmail;
import com.example.apigmac.servicos.periciaServicos.ServicoCancelarPericia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoCancelarPericiaTest {

    @Mock
    private RepositorioPericia repositorioPericia;

    @Mock
    private ServicoEmail servicoEmail;

    @InjectMocks
    private ServicoCancelarPericia servico;

    private Pericia pericia;
    private Paciente paciente;
    private UUID id;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID();

        paciente = new Paciente();
        paciente.setNome("João");
        paciente.setEmail("joao@email.com");
        paciente.setStatusSolicitacao(StatusSolicitacao.PENDENTE);

        pericia = new Pericia();
        pericia.setId(id);
        pericia.setPaciente(paciente);
        pericia.setStatusPericia(StatusPericia.AGENDADA);
    }

    // ---------------- CANCELAR ----------------

    @Test
    void deveCancelarPericiaComSucesso() {

        when(repositorioPericia.findById(id))
                .thenReturn(Optional.of(pericia));

        servico.cancelarPericia(id);

        assertEquals(StatusPericia.CANCELADA, pericia.getStatusPericia());
        assertEquals(StatusSolicitacao.FINALIZADA,
                pericia.getPaciente().getStatusSolicitacao());

        verify(servicoEmail).enviarEmailTexto(any(), any(), any());
        verify(repositorioPericia).save(pericia);
    }

    @Test
    void deveLancarExcecaoQuandoPericiaNaoEncontrada() {

        when(repositorioPericia.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> servico.cancelarPericia(id));
    }

    @Test
    void deveLancarExcecaoQuandoPericiaFinalizada() {

        pericia.setStatusPericia(StatusPericia.FINALIZADA);

        when(repositorioPericia.findById(id))
                .thenReturn(Optional.of(pericia));

        assertThrows(IllegalStateException.class,
                () -> servico.cancelarPericia(id));
    }

    // ---------------- REMARCAR ----------------

    @Test
    void deveRemarcarPericiaComSucesso() {

        when(repositorioPericia.findById(id))
                .thenReturn(Optional.of(pericia));

        LocalDateTime novaData = LocalDateTime.now().plusDays(2);

        servico.remarcarPericia(id, novaData);

        assertEquals(StatusPericia.AGENDADA, pericia.getStatusPericia());
        assertEquals(novaData, pericia.getDataPericia());

        verify(servicoEmail).enviarEmailTexto(any(), any(), any());
        verify(repositorioPericia).save(pericia);
    }

    @Test
    void deveLancarExcecaoQuandoDataForNula() {

        when(repositorioPericia.findById(id))
                .thenReturn(Optional.of(pericia));

        assertThrows(IllegalArgumentException.class,
                () -> servico.remarcarPericia(id, null));
    }

    @Test
    void deveLancarExcecaoQuandoDataForPassado() {

        when(repositorioPericia.findById(id))
                .thenReturn(Optional.of(pericia));

        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);

        assertThrows(IllegalArgumentException.class,
                () -> servico.remarcarPericia(id, dataPassada));
    }
}
