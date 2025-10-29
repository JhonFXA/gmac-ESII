package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.medico.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioMed extends JpaRepository<Medico, UUID> {
}
