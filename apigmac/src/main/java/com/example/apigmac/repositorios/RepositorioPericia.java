package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Pericia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface RepositorioPericia extends JpaRepository<Pericia, UUID>, JpaSpecificationExecutor<Pericia> {
    boolean existsByDocumentacaoId(UUID documentacaoId);
}
