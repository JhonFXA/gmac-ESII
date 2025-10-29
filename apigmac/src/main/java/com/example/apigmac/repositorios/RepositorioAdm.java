package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.administrador.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioAdm extends JpaRepository<Administrador, UUID> {
}
