package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoBuscarUsuario;
import com.example.apigmac.utils.CpfUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicoBuscarUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoBuscarUsuario servicoBuscarUsuario;

    private Usuario usuario;
    private final String CPF_TESTE = "52998224725";

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setLogin("usuarioTeste");
        usuario.setEmail("teste@example.com");
        usuario.setCpf(CPF_TESTE);
        usuario.setNome("João da Silva");
        usuario.setPerfil(Perfil.ADMINISTRADOR);
        usuario.setDataNascimento(LocalDate.of(1990, 5, 10));
    }

    @Test
    void deveRetornarExibeUsuarioDTOQuandoUsuarioExistePorCpf() {

        when(repositorioUsuario.findByCpf(CPF_TESTE))
                .thenReturn(usuario);

        ExibeUsuarioDTO dto =
                servicoBuscarUsuario.buscarUsuario(CPF_TESTE);

        assertNotNull(dto);
        assertEquals(usuario.getLogin(), dto.login());
        assertEquals(usuario.getEmail(), dto.email());

        // ✅ CORREÇÃO AQUI → CPF formatado
        assertEquals(CpfUtils.formatar(usuario.getCpf()), dto.cpf());

        assertEquals(usuario.getNome(), dto.nome());
        assertEquals(usuario.getPerfil(), dto.perfil());
        assertEquals(usuario.getDataNascimento(), dto.dataNascimento());

        verify(repositorioUsuario, times(1))
                .findByCpf(CPF_TESTE);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistePorCpf() {

        String cpfInexistente = "00000000000";

        when(repositorioUsuario.findByCpf(cpfInexistente))
                .thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> servicoBuscarUsuario.buscarUsuario(cpfInexistente)
        );

        assertTrue(exception.getMessage()
                .contains("não encontrado"));

        verify(repositorioUsuario, times(1))
                .findByCpf(cpfInexistente);
    }
}
