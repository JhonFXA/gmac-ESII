package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoListarUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ServicoListarUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoListarUsuario servico;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLogin("login1");
        usuario.setEmail("teste@email.com");
        usuario.setCpf("12345678901");
        usuario.setNome("João");
        usuario.setPerfil(Perfil.ADMINISTRADOR);
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
    }

    @Test
    void deveListarUsuariosOrdenacaoCrescente() {

        when(repositorioUsuario.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(usuario));

        List<ExibeUsuarioDTO> resultado =
                servico.listarUsuarios("João", "123", Perfil.ADMINISTRADOR, false);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        ExibeUsuarioDTO dto = resultado.get(0);
        assertEquals("login1", dto.login());
        assertEquals("teste@email.com", dto.email());
        assertEquals("João", dto.nome());
        assertEquals(Perfil.ADMINISTRADOR, dto.perfil());

        verify(repositorioUsuario, times(1))
                .findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void deveListarUsuariosOrdenacaoDecrescente() {

        when(repositorioUsuario.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(usuario));

        List<ExibeUsuarioDTO> resultado =
                servico.listarUsuarios(null, null, null, true);

        assertEquals(1, resultado.size());

        verify(repositorioUsuario, times(1))
                .findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirUsuarios() {

        when(repositorioUsuario.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of());

        List<ExibeUsuarioDTO> resultado =
                servico.listarUsuarios(null, null, null, false);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
