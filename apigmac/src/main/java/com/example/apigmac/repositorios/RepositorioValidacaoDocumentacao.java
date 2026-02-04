package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.ValidacaoDocumentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioValidacaoDocumentacao extends JpaRepository<ValidacaoDocumentacao, UUID> {
    Optional<ValidacaoDocumentacao>
    findFirstByDocumentacaoIdOrderByDataValidacaoDesc(UUID documentacaoId);
}
