package com.example.apigmac.repositorios;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;





@DataJpaTest
@ActiveProfiles("test")
class RepositorioUsuarioTest {

    @Autowired
    RepositorioUsuario repositorio;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Deve retornar sucesso")
    void findByLoginSucesso() {

        RegistroUsuarioDTO usuario = new RegistroUsuarioDTO(
                "usuarioTeste",           // login
                "usuario@teste.com",      // email
                "senha123",               // senha (sem criptografia)
                "123.456.789-00",         // CPF
                "Usuário Teste",          // nome
                Perfil.RECEPCIONISTA,            // perfil
                LocalDate.now()                // data de nascimento (ex: hoje)
        );

        this.criarUsuario(usuario);

        UserDetails usuarioEncontrado = this.repositorio.findByLogin(usuario.login());

        assertThat(usuarioEncontrado).isNotNull();
        assertThat(usuarioEncontrado.getUsername()).isEqualTo("usuarioTeste");

    }
    @Test
    @DisplayName("Deve retornar que usuario não existe")
    void findByLoginInexistente() {


        UserDetails usuarioEncontrado = this.repositorio.findByLogin("usuarioInexistente");

        assertThat(usuarioEncontrado).isNull();

    }

    private Usuario criarUsuario(RegistroUsuarioDTO dados){
        String senhaCriptografada = new BCryptPasswordEncoder().encode(dados.senha());
        Usuario usuario = new Usuario(dados.login(), dados.email(), senhaCriptografada, dados.cpf(), dados.nome(), dados.perfil(), dados.dataNascimento());
        this.entityManager.persist(usuario);
        return usuario;
    }
}