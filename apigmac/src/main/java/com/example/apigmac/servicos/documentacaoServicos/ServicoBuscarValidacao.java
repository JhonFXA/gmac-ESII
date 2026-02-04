package com.example.apigmac.servicos.documentacaoServicos;

import com.example.apigmac.DTOs.ValidacaoLogDTO;
import com.example.apigmac.entidades.ValidacaoDocumentacao;
import com.example.apigmac.repositorios.RepositorioValidacaoDocumentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ServicoBuscarValidacao {


    @Autowired
    private RepositorioValidacaoDocumentacao repositorioValidacaoDocumentacao;
    public ValidacaoLogDTO buscarValidacaoPorId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        UUID documentacaoId;
        try {
            documentacaoId = UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        ValidacaoDocumentacao validacaoDocumentacao =
                repositorioValidacaoDocumentacao
                        .findFirstByDocumentacaoIdOrderByDataValidacaoDesc(documentacaoId)
                        .orElseThrow(() -> new NoSuchElementException(
                                "Nenhuma validação encontrada para essa documentação"
                        ));

        return new ValidacaoLogDTO(
                validacaoDocumentacao.getUsuario().getNome(),
                validacaoDocumentacao.getPaciente().getNome(),
                validacaoDocumentacao.getStatusValidacaoDocumentacao(),
                validacaoDocumentacao.getDocumentacao().getStatusDocumentacao(),
                validacaoDocumentacao.getObservacao(),
                validacaoDocumentacao.getDataValidacao()
        );
    }

}
