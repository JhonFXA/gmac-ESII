package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoAlterarUsuario;
import com.example.apigmac.utils.CpfUtils;
import com.example.apigmac.utils.ServicoVerificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
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

    private final String CPF_COM_MASCARA = "123.456.789-01";
    private final String CPF_NORMALIZADO = "12345678901";

    @BeforeEach
    void setUp() {
        usuarioExistente = new Usuario();
        usuarioExistente.setNome("Usuario Original");
        usuarioExistente.setCpf(CPF_NORMALIZADO);
        usuarioExistente.setLogin("login.original");
        usuarioExistente.setEmail("original@email.com");
    }

    @Test
    void deveAlterarNomeELoginComSucesso() {

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                "novo.login", null, null,CPF_NORMALIZADO,
                "Novo Nome", null, null, LocalDate.now()
        );

        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(repositorioUsuario.findByCpf(CPF_NORMALIZADO))
                .thenReturn(usuarioExistente);
        when(verificacao.textoObrigatorioValido("Novo Nome", 3))
                .thenReturn(true);
        when(repositorioUsuario.findByLogin("novo.login"))
                .thenReturn(null);

        servico.alterarUsuario(dto, CPF_COM_MASCARA);

        assertEquals("Novo Nome", usuarioExistente.getNome());
        assertEquals("novo.login", usuarioExistente.getLogin());
    }

    @Test
    void deveCriptografarSenhaQuandoInformada() {

        String novaSenha = "Forte@123Password";

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                null, null, novaSenha,
                CPF_COM_MASCARA,
                null, null, null, null
        );

        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(repositorioUsuario.findByCpf(CPF_NORMALIZADO))
                .thenReturn(usuarioExistente);
        when(verificacao.senhaValida(novaSenha)).thenReturn(true);
        when(passwordEncoder.encode(novaSenha)).thenReturn("hash_seguro");

        servico.alterarUsuario(dto, CPF_COM_MASCARA);

        assertEquals("hash_seguro", usuarioExistente.getSenha());
        verify(passwordEncoder).encode(novaSenha);
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {

        String emailExistente = "ja@existe.com";

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                null, emailExistente, null,
                CPF_COM_MASCARA,
                null, null, null, null
        );

        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(repositorioUsuario.findByCpf(CPF_NORMALIZADO))
                .thenReturn(usuarioExistente);
        when(repositorioUsuario.findByEmail(emailExistente))
                .thenReturn(new Usuario());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servico.alterarUsuario(dto, CPF_COM_MASCARA)
        );

        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    void deveAlterarPerfilEDataNascimento() {

        LocalDate dataValida = LocalDate.of(1990, 5, 15);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                null, null, null,
                CPF_COM_MASCARA,
                null, Perfil.ADMINISTRADOR, null, dataValida
        );

        when(verificacao.cpfValido(anyString())).thenReturn(true);
        when(repositorioUsuario.findByCpf(CPF_NORMALIZADO))
                .thenReturn(usuarioExistente);
        when(verificacao.dataNascimentoValida(dataValida))
                .thenReturn(true);

        servico.alterarUsuario(dto, CPF_COM_MASCARA);

        assertEquals(Perfil.ADMINISTRADOR, usuarioExistente.getPerfil());
        assertEquals(dataValida, usuarioExistente.getDataNascimento());
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoEncontrado() {

        when(repositorioUsuario.findByCpf(anyString()))
                .thenReturn(null);

        AlterarUsuarioDTO dto = new AlterarUsuarioDTO(
                null, null, null,
                CPF_COM_MASCARA,
                null, null, null, null
        );

        assertThrows(RuntimeException.class,
                () -> servico.alterarUsuario(dto, CPF_COM_MASCARA));
    }

}
