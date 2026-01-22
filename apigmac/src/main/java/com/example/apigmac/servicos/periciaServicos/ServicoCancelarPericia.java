package com.example.apigmac.servicos.periciaServicos;

import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.servicos.emailServicos.ServicoEmail;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ServicoCancelarPericia {
    @Autowired
    private RepositorioPericia repositorioPericia;

    @Autowired
    private ServicoEmail servicoEmail;

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

        String nomePaciente = pericia.getPaciente().getNome().toUpperCase(Locale.forLanguageTag("pt-BR"));
        String email = pericia.getPaciente().getEmail();
        servicoEmail.enviarEmailTexto(
                email,
                "Perícia Médica Cancelada – Solicitação de Medicação",
                """
                Prezado(a) Sr(a). %s,
                
                Informamos que a perícia médica da sua solicitação
                de medicação foi CANCELADA.
                
                No momento, não há necessidade de comparecimento para realização de perícia.
                Caso novas etapas sejam necessárias, o(a) senhor(a) será informado(a)
                pelos canais oficiais.
                
                Esta mensagem é automática e enviada pelo sistema de gerenciamento
                de solicitações de medicação.
                
                Atenciosamente,
                Sistema GMAC
                """.formatted(nomePaciente)
                        .toUpperCase(Locale.forLanguageTag("pt-BR"))

        );
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

        String nomePaciente = pericia.getPaciente().getNome().toUpperCase(Locale.forLanguageTag("pt-BR"));
        String email = pericia.getPaciente().getEmail();


        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        servicoEmail.enviarEmailTexto(
                email,
                "Perícia Médica Remarcada – Solicitação de Medicação",
                """
                Prezado(a) Sr(a). %s,

                Informamos que sua pericia foi remarcada, pois tivemos um imprevisto.

                O reagendamento da perícia foi realizado para dia %s.

                Esta mensagem é automática e enviada pelo sistema de gerenciamento
                de solicitações de medicação.

                Atenciosamente,
                Sistema GMAC
                """.formatted(nomePaciente, data.format(formatter)));
        pericia.setStatusPericia(StatusPericia.AGENDADA);
        pericia.setDataPericia(data);
        repositorioPericia.save(pericia);
    }
}

