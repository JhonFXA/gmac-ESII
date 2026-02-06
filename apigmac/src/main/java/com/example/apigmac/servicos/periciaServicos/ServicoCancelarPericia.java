package com.example.apigmac.servicos.periciaServicos;

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

    // Repositório responsável por recuperar e persistir o estado da perícia
    @Autowired
    private RepositorioPericia repositorioPericia;

    // Serviço utilizado para notificar o paciente sobre mudanças no agendamento
    @Autowired
    private ServicoEmail servicoEmail;

    /**
     * Cancela uma perícia desde que ela ainda não tenha sido finalizada,
     * encerrando também a solicitação associada ao paciente.
     */
    @Transactional
    public void cancelarPericia(UUID id){

        // Garante que a perícia exista antes de qualquer alteração de estado
        Pericia pericia = repositorioPericia
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Perícia não encontrada")
                );

        // Impede cancelamento de perícias já concluídas, preservando o histórico
        if (pericia.getStatusPericia().equals(StatusPericia.FINALIZADA)){
            throw new IllegalStateException("Perícia Finalizada,Não pode ser cancelada");
        }

        // Padroniza o nome do paciente para envio de comunicação oficial
        String nomePaciente =
                pericia.getPaciente().getNome().toUpperCase(Locale.forLanguageTag("pt-BR"));

        // Envio de notificação para informar o encerramento da etapa de perícia
        servicoEmail.enviarEmailTexto(
                "igorseara04@gmail.com",
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
        );

        // Atualiza o status da perícia e finaliza a solicitação do paciente
        pericia.setStatusPericia(StatusPericia.CANCELADA);
        pericia.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);

        repositorioPericia.save(pericia);
    }

    /**
     * Remarca uma perícia desde que ela ainda esteja ativa,
     * validando a nova data e notificando o paciente.
     */
    @Transactional
    public void remarcarPericia(UUID id, LocalDateTime data){

        // Recupera a perícia garantindo sua existência
        Pericia pericia = repositorioPericia
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Perícia não encontrada")
                );

        // Impede alteração de perícias que já foram finalizadas
        if (pericia.getStatusPericia().equals(StatusPericia.FINALIZADA)){
            throw new IllegalStateException("Perícia Finalizada,Não pode ser remarcada");
        }

        // Validações para evitar inconsistências no agendamento
        if (data == null) {
            throw new IllegalArgumentException("Data é obrigatória para perícia.");
        }
        if (data.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data da perícia não pode ser no passado.");
        }

        String nomePaciente =
                pericia.getPaciente().getNome().toUpperCase(Locale.forLanguageTag("pt-BR"));
        String email = pericia.getPaciente().getEmail();

        // Formatação padronizada da data para comunicação com o paciente
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

        // Notifica o paciente sobre o reagendamento da perícia
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
                """.formatted(nomePaciente, data.format(formatter))
        );

        // Retorna a perícia para o estado de agendada com a nova data definida
        pericia.setStatusPericia(StatusPericia.AGENDADA);
        pericia.setDataPericia(data);

        repositorioPericia.save(pericia);
    }
}
