package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.entidades.*;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.repositorios.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoMarcarPericia {

    @Autowired
    private RepositorioPericia repositorioPericia;

    public void marcarPericia(PericiaDTO dto){
        if(dto == null){
            throw new IllegalArgumentException("Dados da pericia não informados");
        }

        if (dto.paciente()== null ||
                dto.documentacao()== null ||
                dto.usuario() == null ||
                dto.dataPericia()== null) {

            throw new IllegalArgumentException("Algum campo obrigatório está null no DTO");
        }

        if(repositorioPericia.existsByDocumentacaoId(dto.documentacao().getId())){
            throw new IllegalArgumentException("Documentacao com pericia ja marcada");
        }

//        Paciente paciente = repositorioPaciente.findById(dto.paciente())
//                .orElseThrow(() -> new EntityNotFoundException("Paciente não encontrado com ID: " + dto.paciente()));
//
//        Usuario usuario = repositorioUsuario.findById(dto.usuario())
//                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado com ID: " + dto.usuario()));
//
//        Documentacao documentacao = repositorioDocumentacao.findById(dto.documentacao())
//                .orElseThrow(() -> new EntityNotFoundException("Documentacao não encontrada com ID: " + dto.documentacao()));

        Pericia pericia = new Pericia();
        pericia.setDataPericia(dto.dataPericia());
        pericia.setStatusPericia(StatusPericia.AGENDADA);
        pericia.setPaciente(dto.paciente());
        pericia.setUsuario(dto.usuario());
        pericia.setDocumentacao(dto.documentacao());

        repositorioPericia.save(pericia);

    };


}
