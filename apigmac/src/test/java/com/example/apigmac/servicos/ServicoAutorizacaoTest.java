package com.example.apigmac.servicos;

import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ServicoAutorizacaoTest {

    @Mock
    private RepositorioUsuario repositorioUsuario;

    @InjectMocks
    private ServicoAutorizacao servicoAutorizacao;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Criando usuário de teste
        String senhaCriptografada = new BCryptPasswordEncoder().encode("senha123");
        usuario = new Usuario(
                "usuarioTeste",
                "usuario@teste.com",
                senhaCriptografada,
                "123.456.789-00",
                "Usuário Teste",
                Perfil.RECEPCIONISTA,
                new Date()
        );
    }

    @Test
    @DisplayName("Deve retornar UserDetails quando usuário existe")
    void loadUserByUsernameSucesso() {
        // Mock do repositório retornando o usuário
        when(repositorioUsuario.findByLogin("usuarioTeste")).thenReturn(usuario);

        UserDetails userDetails = servicoAutorizacao.loadUserByUsername("usuarioTeste");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("usuarioTeste");
        assertThat(userDetails.getPassword()).isNotEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void loadUserByUsernameUsuarioNaoExiste() {
        // Mock do repositório retornando null
        when(repositorioUsuario.findByLogin("usuarioInexistente")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> servicoAutorizacao.loadUserByUsername("usuarioInexistente"));
    }
}
