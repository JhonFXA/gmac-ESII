package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.DocumentoDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.utils.DocumentacaoSpecs;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.utils.CpfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ServicoListarDocumentacao {

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    public Page<DocumentoDTO> listarDocumentos(
            String cpf,
            String nome,
            StatusDocumentacao status,
            boolean decrescente,
            int pagina,
            int tamanho) {

        if (pagina < 0) {
            throw new IllegalArgumentException("A página não pode ser negativa");
        }

        if (tamanho <= 0) {
            throw new IllegalArgumentException("O tamanho da página deve ser maior que zero");
        }

        String cpfNormalizado = CpfUtils.normalizar(cpf);


        // 1. Definindo a Ordenação (Igual ao anterior)
        Sort sort;
        if (decrescente) {
            sort = Sort.by("dataEnvio").descending();
        } else {
            sort = Sort.by("dataEnvio").ascending();
        }

        // 2. Criando o objeto de Paginação
        Pageable pageable = PageRequest.of(pagina, tamanho, sort);

        // 3. Definindo o Status Default
        StatusDocumentacao statusParaFiltrar;
        if (status == null) {
            statusParaFiltrar = StatusDocumentacao.PENDENTE;
        } else {
            statusParaFiltrar = status;
        }

        // 4. Executando a busca paginada
        Specification<Documentacao> spec = DocumentacaoSpecs.filtrar(cpfNormalizado, nome, statusParaFiltrar);
        Page<Documentacao> paginaEntidades = repositorioDocumentacao.findAll(spec, pageable);

        return paginaEntidades.map(doc -> new DocumentoDTO(
                doc.getId().toString(),
                CpfUtils.formatar(doc.getPaciente().getCpf()),
                doc.getPaciente().getNome(),
                doc.getDataEnvio().toString(),
                doc.getStatusDocumentacao().name()
        ));
    }
}

