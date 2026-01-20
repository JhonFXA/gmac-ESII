package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Documentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface RepositorioDocumentacao extends JpaRepository<Documentacao, UUID>, JpaSpecificationExecutor<Documentacao> {
}
