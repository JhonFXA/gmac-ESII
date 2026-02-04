package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Administrador;
import com.example.apigmac.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioAdm extends JpaRepository<Administrador, UUID> {
    Administrador findByUsuarioId(UUID usuarioId);
}
