package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.ValidacaoDocumentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioValidacaoDocumentacao extends JpaRepository<ValidacaoDocumentacao, UUID> {
}
