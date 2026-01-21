package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface RepositorioUsuario extends JpaRepository<Usuario, UUID>, JpaSpecificationExecutor<Usuario> {
    UserDetails findByLogin(String login);
    UserDetails findByCpf(String cpf);
    UserDetails findByEmail(String email);
}
