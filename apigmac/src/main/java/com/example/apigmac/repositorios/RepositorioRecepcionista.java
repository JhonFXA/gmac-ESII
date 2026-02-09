package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Recepcionista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioRecepcionista extends JpaRepository<Recepcionista, UUID>{
    Recepcionista findByUsuarioId(UUID usuarioId);

}
