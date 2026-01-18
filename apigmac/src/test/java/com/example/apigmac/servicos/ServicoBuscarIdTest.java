package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoBuscarIdTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoBuscarId servicoBuscarId;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLogin("usuarioTeste");
        usuario.setEmail("teste@example.com");
        usuario.setCpf("52998224725");
        usuario.setNome("João da Silva");
        usuario.setPerfil(Perfil.ADMINISTRADOR);
        usuario.setDataNascimento(LocalDate.of(1990, 5, 10));
    }

    @Test
    void deveRetornarRegistroUsuarioDTOQuandoUsuarioExiste() {
        when(repositorioUsuario.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        ExibeUsuarioDTO dto = servicoBuscarId.buscarUsuario(usuario.getId());

        assertNotNull(dto);
        assertEquals(usuario.getLogin(), dto.login());
        assertEquals(usuario.getEmail(), dto.email());
        assertEquals(usuario.getCpf(), dto.cpf());
        assertEquals(usuario.getNome(), dto.nome());
        assertEquals(usuario.getPerfil(), dto.perfil());
        assertEquals(usuario.getDataNascimento(), dto.dataNascimento());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        UUID idInexistente = UUID.randomUUID();
        when(repositorioUsuario.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> servicoBuscarId.buscarUsuario(idInexistente));

        assertTrue(exception.getMessage().contains("Usuário com ID"));
    }
}
