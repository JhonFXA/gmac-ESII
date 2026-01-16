package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Documentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioDocumentacao extends JpaRepository<Documentacao, UUID> {
}
