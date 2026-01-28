package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoAlterarUsuario;
import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ServicoAlterarUsuarioTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @Mock
    private ServicoVerificacao verificacao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ServicoAlterarUsuario servico;

    private Usuario usuarioExistente;
    private final String CPF_TESTE = "123.456.789-01";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuarioExistente = new Usuario();
        usuarioExistente.setNome("Usuario Original");
        usuarioExistente.setCpf(CPF_TESTE);
        usuarioExistente.setLogin("login.original");
        usuarioExistente.setEmail("original@email.com");
    }

    @Test
    void deveAlterarNomeELoginComSucesso() {
        // Ordem do DTO: login, email, senha, cpf, nome, perfil, dataNascimento
        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                "novo.login", null, null, CPF_TESTE, "Novo Nome", null, null
        );

        when(repositorioUsuario.findByCpf(CPF_TESTE)).thenReturn(usuarioExistente);
        when(verificacao.textoObrigatorioValido("Novo Nome", 3)).thenReturn(true);
        when(repositorioUsuario.findByLogin("novo.login")).thenReturn(null);

        // Act
        servico.alterarUsuario(dto, CPF_TESTE);

        // Assert
        assertEquals("Novo Nome", usuarioExistente.getNome());
        assertEquals("novo.login", usuarioExistente.getLogin());
        verify(repositorioUsuario).findByLogin("novo.login");
    }

    @Test
    void deveCriptografarSenhaQuandoInformada() {
        // Ordem do DTO: login, email, senha, cpf, nome, perfil, dataNascimento
        String novaSenha = "Forte@123Password";
        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                null, null, novaSenha, CPF_TESTE, null, null, null
        );

        when(repositorioUsuario.findByCpf(CPF_TESTE)).thenReturn(usuarioExistente);
        when(verificacao.senhaValida(novaSenha)).thenReturn(true);
        when(passwordEncoder.encode(novaSenha)).thenReturn("hash_seguro");

        // Act
        servico.alterarUsuario(dto, CPF_TESTE);

        // Assert
        assertEquals("hash_seguro", usuarioExistente.getSenha());
        verify(passwordEncoder).encode(novaSenha);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        String emailExistente = "ja@existe.com";
        // Ordem do DTO: login, email, senha, cpf, nome, perfil, dataNascimento
        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                null, emailExistente, null, CPF_TESTE, null, null, null
        );

        when(repositorioUsuario.findByCpf(CPF_TESTE)).thenReturn(usuarioExistente);
        // Simula que o repositório achou outro usuário com esse e-mail
        when(repositorioUsuario.findByEmail(emailExistente)).thenReturn(new Usuario());

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                servico.alterarUsuario(dto, CPF_TESTE)
        );
        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    void deveAlterarPerfilEDataNascimento() {
        LocalDate dataValida = LocalDate.of(1990, 5, 15);
        // Ordem do DTO: login, email, senha, cpf, nome, perfil, dataNascimento
        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                null, null, null, CPF_TESTE, null, Perfil.ADMINISTRADOR, dataValida
        );

        when(repositorioUsuario.findByCpf(CPF_TESTE)).thenReturn(usuarioExistente);
        when(verificacao.dataNascimentoValida(dataValida)).thenReturn(true);

        // Act
        servico.alterarUsuario(dto, CPF_TESTE);

        // Assert
        assertEquals(Perfil.ADMINISTRADOR, usuarioExistente.getPerfil());
        assertEquals(dataValida, usuarioExistente.getDataNascimento());
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoEncontrado() {
        // Arrange
        when(repositorioUsuario.findByCpf(anyString())).thenReturn(null);
        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(null, null, null, "000", null, null, null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> servico.alterarUsuario(dto, CPF_TESTE));
    }
}