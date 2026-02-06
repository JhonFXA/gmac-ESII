package com.example.apigmac.servicos.documentacaoServicos;

import com.example.apigmac.DTOs.DocumentoDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.utils.DocumentacaoSpecs;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.utils.CpfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoListarDocumentacao {

    // Repositório responsável pelo acesso às documentações no banco de dados
    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    /**
     * Realiza a listagem de documentações com base em filtros opcionais
     * como CPF, nome do paciente, status e ordenação por data de envio.
     */
    public List<DocumentoDTO> listarDocumentos(
            String cpf,
            String nome,
            StatusDocumentacao status,
            boolean decrescente) {

        // Normalização do CPF para garantir consistência na busca
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        // Definição da ordenação com base na data de envio
        Sort sort;
        if (decrescente) {
            sort = Sort.by("dataEnvio").descending();
        } else {
            sort = Sort.by("dataEnvio").ascending();
        }

        // Definição do status padrão quando nenhum status é informado
        StatusDocumentacao statusParaFiltrar;
        if (status == null) {
            statusParaFiltrar = StatusDocumentacao.PENDENTE;
        } else {
            statusParaFiltrar = status;
        }

        // Criação da Specification para aplicação dos filtros dinâmicos
        Specification<Documentacao> spec =
                DocumentacaoSpecs.filtrar(cpfNormalizado, nome, statusParaFiltrar);

        // Execução da consulta com filtros e ordenação
        List<Documentacao> paginaEntidades =
                repositorioDocumentacao.findAll(spec, sort);

        // Conversão das entidades em DTOs para retorno ao controller
        return paginaEntidades.stream().map(doc -> new DocumentoDTO(
                doc.getId().toString(),
                CpfUtils.formatar(doc.getPaciente().getCpf()),
                doc.getPaciente().getNome(),
                doc.getDataEnvio().toString(),
                doc.getStatusDocumentacao().name()
        )).toList();
    }
}
