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

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    public DocumentoDTO buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Id do documento inválido");
        }

        Documentacao doc = repositorioDocumentacao.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Documento não encontrado"));

        return new DocumentoDTO(
                doc.getId().toString(),
                CpfUtils.formatar(doc.getPaciente().getCpf()),
                doc.getPaciente().getNome(),
                doc.getDataEnvio().toString(),
                doc.getStatusDocumentacao().name()
        );
    }
}
