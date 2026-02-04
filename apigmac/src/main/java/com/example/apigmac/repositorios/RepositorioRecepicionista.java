package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Recepcionista;
import com.example.apigmac.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioRecepicionista extends JpaRepository<Recepcionista, UUID>{
    Recepcionista findByUsuarioId(UUID usuarioId);

}
