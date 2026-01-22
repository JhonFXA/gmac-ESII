package com.example.apigmac.servicos.periciaServicos;

import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioPericia;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ServicoCancelarPericia {
    @Autowired
    private RepositorioPericia repositorioPericia;

    @Transactional
    public void cancelarPericia(UUID id){

        Pericia pericia = repositorioPericia
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Perícia não encontrada")
                );
        if (pericia.getStatusPericia().equals(StatusPericia.FINALIZADA)){
            throw new IllegalStateException("Perícia Finalizada,Não pode ser cancelada");
        }

        pericia.setStatusPericia(StatusPericia.CANCELADA);
        pericia.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
        repositorioPericia.save(pericia);

    }

    @Transactional
    public void remarcarPericia(UUID id,LocalDateTime data){
        Pericia pericia = repositorioPericia
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Perícia não encontrada")
                );
        if (pericia.getStatusPericia().equals(StatusPericia.FINALIZADA)){
            throw new IllegalStateException("Perícia Finalizada,Não pode ser remarcada");
        }

        if (data == null) {
                throw new IllegalArgumentException("Data é obrigatória para perícia.");
        }
        if (data.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("A data da perícia não pode ser no passado.");
        }
        pericia.setStatusPericia(StatusPericia.AGENDADA);
        pericia.setDataPericia(data);
        repositorioPericia.save(pericia);
    }
}

