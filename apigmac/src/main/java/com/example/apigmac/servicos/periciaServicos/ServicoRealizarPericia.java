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

    @Autowired
    private ServicoValidarDocumentacao servicoValidarDocumentacao;

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    @Autowired
    private RepositorioPericia repositorioPericia;

    public void validarPericia(ValidacaoDocumentacaoDTO dto, UUID periciaId){
        Pericia pericia = repositorioPericia.findById(periciaId)
                .orElseThrow(() ->
                        new NoSuchElementException("Perícia não encontrada")
                );

        pericia.setStatusPericia(StatusPericia.FINALIZADA);

        servicoValidarDocumentacao.registrarValidacao(dto);

        repositorioPericia.save(pericia);
    }
}
