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

    // Repositório responsável pelo acesso aos registros de validação da documentação
    @Autowired
    private RepositorioValidacaoDocumentacao repositorioValidacaoDocumentacao;

    /**
     * Realiza a busca da última validação associada a uma documentação específica,
     * retornando as informações necessárias para exibição em histórico ou auditoria.
     */
    public ValidacaoLogDTO buscarValidacaoPorId(String id) {

        // Validação básica do identificador recebido
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        // Conversão do ID para o formato UUID
        UUID documentacaoId;
        try {
            documentacaoId = UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        // Busca da validação mais recente associada à documentação
        ValidacaoDocumentacao validacaoDocumentacao =
                repositorioValidacaoDocumentacao
                        .findFirstByDocumentacaoIdOrderByDataValidacaoDesc(documentacaoId)
                        .orElseThrow(() -> new NoSuchElementException(
                                "Nenhuma validação encontrada para essa documentação"
                        ));

        // Montagem do DTO com os dados da validação para retorno ao controller
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
