package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.DTOs.ValidacaoPericiaDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.repositorios.RepositorioValidacaoDocumentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
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
        Optional<Pericia> pericia = repositorioPericia.findById(periciaId);
        if (pericia.isEmpty()){
            throw new RuntimeException("Pericia não encontrada.");
        }

        pericia.get().setStatusPericia(StatusPericia.FINALIZADA);

        servicoValidarDocumentacao.registrarValidacao(dto);

        repositorioPericia.save(pericia.get());
    }
}
