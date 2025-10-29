package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.recepcionista.Recepcionista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioRecepicionista extends JpaRepository<Recepcionista, UUID>{
}
