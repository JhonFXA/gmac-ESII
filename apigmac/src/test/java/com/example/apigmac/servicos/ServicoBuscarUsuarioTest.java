package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoBuscarUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoBuscarUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoBuscarUsuario servicoBuscarUsuario;

    private Usuario usuario;
    private final String CPF_TESTE = "52998224725";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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
        // Arrange: Configura o mock para buscar por CPF
        // Note: Se o seu repositório retorna UserDetails, o cast para Usuario deve funcionar no mock
        when(repositorioUsuario.findByCpf(CPF_TESTE)).thenReturn(usuario);

        // Act: Chama o serviço passando o CPF
        ExibeUsuarioDTO dto = servicoBuscarUsuario.buscarUsuario(CPF_TESTE);

        // Assert
        assertNotNull(dto);
        assertEquals(usuario.getLogin(), dto.login());
        assertEquals(usuario.getEmail(), dto.email());
        assertEquals(usuario.getCpf(), dto.cpf());
        assertEquals(usuario.getNome(), dto.nome());
        assertEquals(usuario.getPerfil().toString(), dto.perfil().toString());

        verify(repositorioUsuario, times(1)).findByCpf(CPF_TESTE);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistePorCpf() {
        // Arrange: Quando buscar um CPF que não existe, retorna null
        String cpfInexistente = "00000000000";
        when(repositorioUsuario.findByCpf(cpfInexistente)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> servicoBuscarUsuario.buscarUsuario(cpfInexistente));

        // Ajuste a mensagem conforme o que está escrito no seu ServicoBuscarUsuario
        assertTrue(exception.getMessage().contains("Usuario não encontrado") ||
                exception.getMessage().contains("não encontrado"));
    }
}