package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoAlterarUsuarioTest {

    @InjectMocks
    private ServicoAlterarUsuario servicoAlterarUsuario;

    @Mock
    private ServicoVerificacao verificacao;

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario usuarioExistente;
    private UUID idUsuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        idUsuario = UUID.randomUUID();

        usuarioExistente = new Usuario(
                "loginAntigo",
                "antigo@email.com",
                "senhaAntiga",
                "12345678900",
                "Nome Antigo",
                Perfil.RECEPCIONISTA,
                LocalDate.of(2000, 1, 1)
        );
        usuarioExistente.setId(idUsuario);

        when(repositorioUsuario.findById(idUsuario)).thenReturn(Optional.of(usuarioExistente));
    }

    @Test
    void deveAlterarNomeComSucesso() {
        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, null, null, null, null, "Novo Nome", null, null);

        servicoAlterarUsuario.alterarUsuario(dto);

        assertEquals("Novo Nome", usuarioExistente.getNome());
        verify(repositorioUsuario).save(usuarioExistente);
    }

    @Test
    void deveAlterarEmailValido() {
        when(verificacao.emailValido("novo@email.com")).thenReturn(true);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, null, "novo@email.com", null, null, null, null, null);

        servicoAlterarUsuario.alterarUsuario(dto);

        assertEquals("novo@email.com", usuarioExistente.getEmail());
        verify(verificacao).emailValido("novo@email.com");
        verify(repositorioUsuario).save(usuarioExistente);
    }

    @Test
    void deveLancarExcecaoQuandoEmailInvalido() {
        when(verificacao.emailValido("emailErrado")).thenReturn(false);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, null, "emailErrado", null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> servicoAlterarUsuario.alterarUsuario(dto));
        verify(repositorioUsuario, never()).save(any());
    }

    @Test
    void deveAlterarCpfValido() {
        when(verificacao.cpfValido("11122233344")).thenReturn(true);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, null, null, null, "11122233344", null, null, null);

        servicoAlterarUsuario.alterarUsuario(dto);

        assertEquals("11122233344", usuarioExistente.getCpf());
        verify(verificacao).cpfValido("11122233344");
        verify(repositorioUsuario).save(usuarioExistente);
    }

    @Test
    void deveLancarExcecaoQuandoCpfInvalido() {
        when(verificacao.cpfValido("00000000000")).thenReturn(false);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, null, null, null, "00000000000", null, null, null);

        assertThrows(IllegalArgumentException.class, () -> servicoAlterarUsuario.alterarUsuario(dto));
        verify(repositorioUsuario, never()).save(any());
    }

    @Test
    void deveAlterarLoginQuandoNaoExistente() {
        when(repositorioUsuario.findByLogin("novoLogin")).thenReturn(null);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, "novoLogin", null, null, null, null, null, null);

        servicoAlterarUsuario.alterarUsuario(dto);

        assertEquals("novoLogin", usuarioExistente.getLogin());
        verify(repositorioUsuario).save(usuarioExistente);
    }

    @Test
    void deveLancarExcecaoQuandoLoginJaExiste() {
        when(repositorioUsuario.findByLogin("loginExistente")).thenReturn(new Usuario());

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, "loginExistente", null, null, null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> servicoAlterarUsuario.alterarUsuario(dto));
        verify(repositorioUsuario, never()).save(any());
    }

    @Test
    void deveAlterarSenhaValida() {
        when(verificacao.senhaValida("novaSenha123")).thenReturn(true);
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaCriptografada");

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, null, null, "novaSenha123", null, null, null, null);

        servicoAlterarUsuario.alterarUsuario(dto);

        assertEquals("senhaCriptografada", usuarioExistente.getSenha());
        verify(passwordEncoder).encode("novaSenha123");
        verify(repositorioUsuario).save(usuarioExistente);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaInvalida() {
        when(verificacao.senhaValida("123")).thenReturn(false);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idUsuario, null, null, "123", null, null, null, null);

        assertThrows(IllegalArgumentException.class, () -> servicoAlterarUsuario.alterarUsuario(dto));
        verify(passwordEncoder, never()).encode(any());
        verify(repositorioUsuario, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        UUID idInexistente = UUID.randomUUID();
        when(repositorioUsuario.findById(idInexistente)).thenReturn(Optional.empty());

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(idInexistente, null, null, null, null, null, null, null);

        assertThrows(RuntimeException.class, () -> servicoAlterarUsuario.alterarUsuario(dto));
    }
}
