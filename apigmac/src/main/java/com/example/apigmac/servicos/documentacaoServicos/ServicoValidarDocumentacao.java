package com.example.apigmac.servicos.documentacaoServicos;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.entidades.ValidacaoDocumentacao;
import com.example.apigmac.modelo.enums.*;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioValidacaoDocumentacao;
import com.example.apigmac.servicos.emailServicos.ServicoEmail;
import com.example.apigmac.servicos.periciaServicos.ServicoMarcarPericia;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
public class ServicoValidarDocumentacao {

    // Serviço responsável pelo envio de notificações por e-mail
    @Autowired
    private ServicoEmail servicoEmail;

    // Repositório de documentações
    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    // Gerenciador de autenticação para validação de credenciais
    @Autowired
    private AuthenticationManager authenticationManager;

    // Repositório de registros de validação
    @Autowired
    private RepositorioValidacaoDocumentacao repositorioValidacaoDocumentacao;

    // Serviço responsável pelo agendamento de perícias
    @Autowired
    private ServicoMarcarPericia servicoMarcarPericia;

    /**
     * Registra a validação de uma documentação, aplicando regras de permissão
     * e atualizando os status relacionados.
     */
    @Transactional
    public void registrarValidacao(ValidacaoDocumentacaoDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Os dados da validação não foram informados.");
        }

        if (dto.login() == null || dto.login().isBlank()) {
            throw new IllegalArgumentException("O login do usuário é obrigatório.");
        }

        if (dto.senha() == null || dto.senha().isBlank()) {
            throw new IllegalArgumentException("A senha do usuário é obrigatória.");
        }

        if (dto.status() == null) {
            throw new IllegalArgumentException("O status da validação deve ser informado.");
        }


        if (dto.observacao() == null || dto.observacao().isBlank()) {
            throw new IllegalArgumentException(
                    "A observação é obrigatória"
            );
        }

        if (dto.observacao().length() < 10) {
            throw new IllegalArgumentException(
                    "A observação deve conter pelo menos 10 caracteres."
            );
        }

        if (dto.observacao().length() > 500) {
            throw new IllegalArgumentException(
                    "A observação não pode ultrapassar 500 caracteres."
            );
        }


        if (dto.status() == StatusValidacaoDocumentacao.PERICIA && dto.data() == null) {
            throw new IllegalArgumentException(
                    "A data da perícia é obrigatória quando o status é PERÍCIA."
            );
        }

        try {
            // Autenticação do usuário responsável pela validação
            var authToken =
                    new UsernamePasswordAuthenticationToken(dto.login(), dto.senha());
            var auth = authenticationManager.authenticate(authToken);

            Usuario usuario = (Usuario) auth.getPrincipal();


            // Recupera a documentação a ser validada
            Documentacao documentacao = repositorioDocumentacao
                    .findById(dto.documentacaoId())
                    .orElseThrow(() ->
                            new NoSuchElementException(
                                    "Documentação não encontrada para o identificador informado."
                            )
                    );
            Paciente paciente = documentacao.getPaciente();

            if(documentacao.getStatusDocumentacao() == StatusDocumentacao.REPROVADA || documentacao.getStatusDocumentacao() == StatusDocumentacao.REPROVADA){
                throw new IllegalStateException("Documentação já avaliada");
            }

            if(paciente.getStatusSolicitacao() == StatusSolicitacao.FINALIZADA){
                throw new IllegalStateException("Solicitação já finalizada, não é possível realizar esta ação");
            }

            // Regra obrigatória para solicitação de perícia
            if (dto.status() == StatusValidacaoDocumentacao.PERICIA && dto.data() == null) {
                throw new IllegalArgumentException(
                        "A data da perícia é obrigatória quando o status é PERICIA."
                );
            }

            // Validação de permissões conforme perfil do usuário
            validarPermissao(usuario, dto.status());

            // Criação do registro de validação
            ValidacaoDocumentacao validacao = new ValidacaoDocumentacao();
            validacao.setDocumentacao(documentacao);
            validacao.setPaciente(documentacao.getPaciente());
            validacao.setUsuario(usuario);
            validacao.setDataValidacao(LocalDate.now());
            validacao.setObservacao(dto.observacao());
            validacao.setStatusValidacaoDocumentacao(dto.status());

            // Atualiza os status da documentação e do paciente conforme decisão
            atualizarStatusRelacionados(
                    documentacao,
                    dto.status(),
                    usuario,
                    dto.data()
            );

            // Persistência das alterações
            repositorioValidacaoDocumentacao.save(validacao);
            repositorioDocumentacao.save(documentacao);

        } catch (AuthenticationException ex) {
            throw new AccessDeniedException("Login ou senha inválidos");
        }
    }

    /**
     * Verifica se o usuário possui permissão para o tipo de validação solicitada.
     */
    private void validarPermissao(
            Usuario usuario,
            StatusValidacaoDocumentacao status
    ) {
        if (usuario.getPerfil() != Perfil.MEDICO) {
            if (status != StatusValidacaoDocumentacao.PERICIA) {
                throw new AccessDeniedException(
                        "Apenas médicos podem APROVAR ou REPROVAR documentos. " +
                                "Seu perfil permite apenas solicitar PERÍCIA."
                );
            }
        }
    }

    /**
     * Atualiza os status da documentação, do paciente e executa
     * ações complementares (e-mail, perícia).
     */
    private void atualizarStatusRelacionados(
            Documentacao doc,
            StatusValidacaoDocumentacao status,
            Usuario usuario,
            LocalDateTime data
    ) {
        String nomePaciente =
                doc.getPaciente().getNome().toUpperCase(Locale.forLanguageTag("pt-BR"));
        String email = doc.getPaciente().getEmail();

        switch (status) {

            case APROVADA -> {
                // Notificação de aprovação
                servicoEmail.enviarEmailTexto(
                        email,
                        "Solicitação de Medicação Aprovada",
                        """
                        Prezado(a) Sr(a). %s,
        
                        Informamos que, após avaliação médica, sua solicitação de medicação foi APROVADA.
        
                        Solicitamos que compareça à unidade de saúde mais próxima para dar continuidade às próximas etapas do processo.
        
                        Atenciosamente,
                        Sistema GMAC
                        """.formatted(nomePaciente)
                );

                doc.setStatusDocumentacao(StatusDocumentacao.APROVADA);

                // Finaliza solicitação se não houver pendências
                boolean existePendenteOuPericia = doc.getPaciente()
                        .getDocumentacoes()
                        .stream()
                        .anyMatch(d ->
                                d.getStatusDocumentacao() == StatusDocumentacao.PENDENTE ||
                                        d.getStatusDocumentacao() == StatusDocumentacao.PERICIA
                        );

                if (!existePendenteOuPericia) {
                    doc.getPaciente()
                            .setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                }
            }

            case REPROVADA -> {
                // Notificação de reprovação
                servicoEmail.enviarEmailTexto(
                        email,
                        "Solicitação de Medicação Reprovada",
                        """
                        Prezado(a) Sr(a). %s,
        
                        Informamos que, após avaliação médica, sua solicitação de medicação foi REPROVADA.
        
                        Atenciosamente,
                        Sistema GMAC
                        """.formatted(nomePaciente)
                );

                doc.setStatusDocumentacao(StatusDocumentacao.REPROVADA);

                boolean existePendenteOuPericia = doc.getPaciente()
                        .getDocumentacoes()
                        .stream()
                        .anyMatch(d ->
                                d.getStatusDocumentacao() == StatusDocumentacao.PENDENTE ||
                                        d.getStatusDocumentacao() == StatusDocumentacao.PERICIA
                        );

                if (!existePendenteOuPericia) {
                    doc.getPaciente()
                            .setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                }
            }

            case PERICIA -> {
                // Validação da data de perícia
                if (!data.isAfter(LocalDateTime.now())) {
                    throw new IllegalArgumentException("DATA INVALIDA");
                }

                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

                doc.setStatusDocumentacao(StatusDocumentacao.PERICIA);
                doc.getPaciente()
                        .setStatusSolicitacao(StatusSolicitacao.PENDENTE);

                // Criação da perícia
                PericiaDTO periciaDTO = new PericiaDTO(
                        data,
                        StatusPericia.AGENDADA,
                        doc.getPaciente(),
                        usuario,
                        doc
                );

                servicoMarcarPericia.marcarPericia(periciaDTO);

                // Notificação de perícia
                servicoEmail.enviarEmailTexto(
                        email,
                        "Perícia Médica Necessária – Solicitação de Medicação",
                        """
                        Prezado(a) Sr(a). %s,
        
                        Informamos que foi identificada a necessidade de realização de perícia médica.
        
                        O agendamento foi realizado para %s.
        
                        Atenciosamente,
                        Sistema GMAC
                        """.formatted(nomePaciente, data.format(formatter))
                                .toUpperCase(Locale.forLanguageTag("pt-BR"))
                );
            }
        }
    }
}
