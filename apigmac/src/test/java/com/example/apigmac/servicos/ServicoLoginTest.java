package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.DTOs.LoginUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.servicos.documentacaoServicos.ServicoLogin;
import com.example.apigmac.servicos.documentacaoServicos.ServicoToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.DisabledException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoLoginTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ServicoToken servicoToken;

    @InjectMocks
    private ServicoLogin servicoLogin;

    private Usuario usuarioAtivo;
    private Usuario usuarioInativo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuarioAtivo = new Usuario();
        usuarioAtivo.setLogin("teste");
        usuarioAtivo.setSenha("123");
        usuarioAtivo.setPerfil(Perfil.ADMINISTRADOR);
        usuarioAtivo.setNome("Nome Teste");
        usuarioAtivo.setEmail("teste@email.com");
        usuarioAtivo.setCpf("12345678900");
        // ADICIONE ESTA LINHA:
        usuarioAtivo.setDataNascimento(LocalDate.of(2000, 1, 1));

        usuarioInativo = new Usuario();
        usuarioInativo.setLogin("inativo");
        usuarioInativo.setSenha("123");
        usuarioInativo.setPerfil(Perfil.INATIVO);
        // Para o usuário inativo, também é boa prática preencher se o código chegar até o DTO
        usuarioInativo.setDataNascimento(LocalDate.of(2000, 1, 1));
    }

    @Test
    void deveRetornarTokenQuandoLoginValido() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("teste", "123");
        Authentication authMock = mock(Authentication.class);

        when(authMock.getPrincipal()).thenReturn(usuarioAtivo);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(servicoToken.gerarToken(usuarioAtivo)).thenReturn("tokenGerado");

        // Act
        LoginUsuarioDTO resultado = servicoLogin.login(loginDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("tokenGerado", resultado.token());
        assertEquals("ADMINISTRADOR", resultado.perfil());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(servicoToken, times(1)).gerarToken(usuarioAtivo);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioInativo() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("inativo", "123");
        Authentication authMock = mock(Authentication.class);

        when(authMock.getPrincipal()).thenReturn(usuarioInativo);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);

        // Act & Assert
        assertThrows(DisabledException.class, () -> servicoLogin.login(loginDTO));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(servicoToken, never()).gerarToken(any());
    }
}
