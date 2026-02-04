package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.PaginaPericiaDTO;
import com.example.apigmac.entidades.*;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.servicos.periciaServicos.ServicoListarPericia;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoListarPericiaTest {

    @Mock
    private RepositorioPericia repositorioPericia;

    @InjectMocks
    private ServicoListarPericia servico;

    private Usuario medico;
    private Pericia pericia;

    @BeforeEach
    void setup() {

        medico = new Usuario();
        medico.setNome("Dr João");
        medico.setPerfil(Perfil.MEDICO);

        Paciente paciente = new Paciente();
        paciente.setNome("Carlos");

        Documentacao doc = new Documentacao();
        doc.setId(UUID.randomUUID());

        pericia = new Pericia();
        pericia.setId(UUID.randomUUID());
        pericia.setPaciente(paciente);
        pericia.setUsuario(medico);
        pericia.setDocumentacao(doc);
        pericia.setStatusPericia(StatusPericia.AGENDADA);
        pericia.setDataPericia(LocalDateTime.now().plusDays(1));
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoAutenticado() {

        SecurityContextHolder.clearContext();

        assertThrows(IllegalStateException.class,
                () -> servico.listarPericia(null, null, null, false));
    }

    @Test
    void medicoDeveFiltrarPeloProprioNome() {

        var auth = new UsernamePasswordAuthenticationToken(
                medico, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(repositorioPericia.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(pericia));

        List<PaginaPericiaDTO> resultado =
                servico.listarPericia(null, "Outro Nome",
                        StatusPericia.AGENDADA, false);

        assertEquals(1, resultado.size());
        assertEquals("Carlos", resultado.get(0).nomePaciente());
        assertEquals("Dr João", resultado.get(0).nomeMedico());
    }

    @Test
    void deveAplicarOrdenacaoCrescente() {

        var auth = new UsernamePasswordAuthenticationToken(
                medico, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);

        when(repositorioPericia.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(pericia));

        servico.listarPericia(null, null, null, false);

        verify(repositorioPericia)
                .findAll(any(Specification.class), captor.capture());

        assertEquals(Sort.Direction.ASC,
                captor.getValue().getOrderFor("dataPericia").getDirection());
    }

    @Test
    void deveAplicarOrdenacaoDecrescente() {

        var auth = new UsernamePasswordAuthenticationToken(
                medico, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);

        when(repositorioPericia.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(pericia));

        servico.listarPericia(null, null, null, true);

        verify(repositorioPericia)
                .findAll(any(Specification.class), captor.capture());

        assertEquals(Sort.Direction.DESC,
                captor.getValue().getOrderFor("dataPericia").getDirection());
    }

    @Test
    void deveConverterEntidadeParaDTOCorretamente() {

        var auth = new UsernamePasswordAuthenticationToken(
                medico, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(repositorioPericia.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(pericia));

        List<PaginaPericiaDTO> resultado =
                servico.listarPericia(null, null,
                        StatusPericia.AGENDADA, false);

        PaginaPericiaDTO dto = resultado.get(0);

        assertEquals(pericia.getId().toString(), dto.id());
        assertEquals(pericia.getDocumentacao().getId().toString(),
                dto.idDocumentacao());
        assertEquals("Carlos", dto.nomePaciente());
        assertEquals("Dr João", dto.nomeMedico());
        assertEquals(StatusPericia.AGENDADA, dto.statusPericia());
        assertNotNull(dto.data());
    }
}
