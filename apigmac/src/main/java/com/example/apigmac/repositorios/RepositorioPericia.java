package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Pericia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioPericia extends JpaRepository<Pericia, UUID> {
    boolean existsByDocumentacaoId(UUID documentacaoId);
}
