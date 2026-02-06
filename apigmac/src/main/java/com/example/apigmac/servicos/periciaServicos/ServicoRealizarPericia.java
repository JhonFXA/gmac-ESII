package com.example.apigmac.servicos.periciaServicos;

import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.servicos.documentacaoServicos.ServicoValidarDocumentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ServicoRealizarPericia {

    // Serviço reutilizado para garantir que a validação da perícia
    // siga as mesmas regras de negócio aplicadas à documentação
    @Autowired
    private ServicoValidarDocumentacao servicoValidarDocumentacao;

    // Repositório utilizado apenas para garantir integridade do fluxo
    // entre perícia e documentação
    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    // Repositório responsável pelo controle do ciclo de vida da perícia
    @Autowired
    private RepositorioPericia repositorioPericia;

    /**
     * Finaliza a perícia e delega a validação da documentação ao serviço responsável,
     * garantindo consistência de regras e centralização da lógica de validação.
     */
    public void validarPericia(ValidacaoDocumentacaoDTO dto, UUID periciaId){

        // Recupera a perícia garantindo que o processo exista antes da validação
        Pericia pericia = repositorioPericia.findById(periciaId)
                .orElseThrow(() ->
                        new NoSuchElementException("Perícia não encontrada")
                );

        // Atualiza o estado da perícia antes da validação da documentação
        // para manter a coerência do fluxo transacional
        pericia.setStatusPericia(StatusPericia.FINALIZADA);
        System.out.println("aaaaaaaaa");

        // Delegação da validação para manter regras centralizadas
        servicoValidarDocumentacao.registrarValidacao(dto);

        // Persistência final da perícia após conclusão do processo
        repositorioPericia.save(pericia);
    }
}
