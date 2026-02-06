package com.example.apigmac.servicos.documentacaoServicos;

import com.example.apigmac.DTOs.DocumentoDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.utils.CpfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ServicoBuscarDocumentacao {

    // Repositório responsável pelo acesso aos dados de documentação no banco
    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    /**
     * Realiza a busca de uma documentação específica pelo seu identificador,
     * retornando os dados necessários para exibição ao usuário.
     */
    public DocumentoDTO buscarPorId(String id) {

        // Validação básica do identificador recebido
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        // Conversão do identificador para o formato UUID
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        // Busca da documentação no repositório
        Documentacao doc = repositorioDocumentacao.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Documento não encontrado"));

        // Montagem do DTO com os dados da documentação para retorno
        return new DocumentoDTO(
                doc.getId().toString(),
                CpfUtils.formatar(doc.getPaciente().getCpf()),
                doc.getPaciente().getNome(),
                doc.getDataEnvio().toString(),
                doc.getStatusDocumentacao().name()
        );
    }
}
