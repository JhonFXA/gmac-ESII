package com.example.apigmac.servicos.documentacaoServicos;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.entidades.Documentacao;
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

    @Autowired
    private ServicoEmail servicoEmail;

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RepositorioValidacaoDocumentacao repositorioValidacaoDocumentacao;

    @Autowired
    private ServicoMarcarPericia servicoMarcarPericia;

    @Transactional
    public void registrarValidacao(ValidacaoDocumentacaoDTO dto) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(dto.login(),dto.senha());
            var auth = authenticationManager.authenticate(authToken);
            Usuario usuario = (Usuario) auth.getPrincipal();

            Documentacao documentacao = repositorioDocumentacao
                    .findById(dto.documentacaoId())
                    .orElseThrow(() ->
                            new NoSuchElementException("Documentação não encontrada")
                    );

            if (dto.status() == StatusValidacaoDocumentacao.PERICIA && dto.data() == null) {
                throw new IllegalArgumentException("A data da perícia é obrigatória quando o status é PERICIA.");
            }

            validarPermissao(usuario, dto.status());

            ValidacaoDocumentacao validacao = new ValidacaoDocumentacao();
            validacao.setDocumentacao(documentacao);
            validacao.setPaciente(documentacao.getPaciente());
            validacao.setUsuario(usuario);
            validacao.setDataValidacao(LocalDate.now());
            validacao.setObservacao(dto.observacao());
            validacao.setStatusValidacaoDocumentacao(dto.status());

            // 5. Atualização dos Status conforme a escolha
            atualizarStatusRelacionados(documentacao, dto.status(),usuario,dto.data());

            // 6. Persistência
            repositorioValidacaoDocumentacao.save(validacao);
            repositorioDocumentacao.save(documentacao); // Garante a atualização do status da doc e paciente

        }catch(AuthenticationException ex) {
                throw new IllegalArgumentException("Credenciais inválidas");
        }

    }

    private void validarPermissao(Usuario usuario, StatusValidacaoDocumentacao status) {

        if (!(usuario.getPerfil() == Perfil.MEDICO)) {
            if (status != StatusValidacaoDocumentacao.PERICIA) {
                throw new AccessDeniedException(
                        "Apenas médicos podem APROVAR ou REPROVAR documentos. Seu perfil permite apenas solicitar PERÍCIA."
                );
            }
        }
    }

private void atualizarStatusRelacionados(
            Documentacao doc,
            StatusValidacaoDocumentacao status,
            Usuario usuario,
            LocalDateTime data
){
            String nomePaciente = doc.getPaciente().getNome().toUpperCase(Locale.forLanguageTag("pt-BR"));
            String email = doc.getPaciente().getEmail();


            switch (status) {
                case APROVADA -> {
                    servicoEmail.enviarEmailTexto(
                            email,
                            "Solicitação de Medicação Aprovada",
                            """
                            Prezado(a) Sr(a). %s,
        
                            Informamos que, após avaliação médica, sua solicitação de medicação foi APROVADA.
        
                            Solicitamos que compareça à unidade de saúde mais próxima para dar continuidade às próximas etapas do processo.
        
                            Esta mensagem é automática e enviada pelo sistema de gerenciamento de solicitações de medicação.
        
                            Atenciosamente,
                            Sistema GMAC
                            """.formatted(nomePaciente)

                    );

                    doc.setStatusDocumentacao(StatusDocumentacao.APROVADA);

                    boolean existePendenteOuPericia = doc.getPaciente()
                            .getDocumentacoes()
                            .stream()
                            .anyMatch(d -> d.getStatusDocumentacao() == StatusDocumentacao.PENDENTE
                                    || d.getStatusDocumentacao() == StatusDocumentacao.PERICIA);


                    if (!existePendenteOuPericia){
                        doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                    }
                }

                case REPROVADA -> {
                    servicoEmail.enviarEmailTexto(
                            email,
                            "Solicitação de Medicação Reprovada",
                            """
                            Prezado(a) Sr(a). %s,
        
                            Informamos que, após avaliação médica, sua solicitação de medicação foi REPROVADA.
        
                            Para mais informações ou esclarecimentos, orientamos que procure a unidade de saúde onde realizou a solicitação.
        
                            Esta mensagem é automática e enviada pelo sistema de gerenciamento de solicitações de medicação.
        
                            Atenciosamente,
                            Sistema GMAC
                            """.formatted(nomePaciente)
                    );

                    doc.setStatusDocumentacao(StatusDocumentacao.REPROVADA);

                    boolean existePendenteOuPericia = doc.getPaciente()
                            .getDocumentacoes()
                            .stream()
                            .anyMatch(d -> d.getStatusDocumentacao() == StatusDocumentacao.PENDENTE
                                    || d.getStatusDocumentacao() == StatusDocumentacao.PERICIA);


                    if (!existePendenteOuPericia){
                        doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                    }
                }

                case PERICIA -> {

                    DateTimeFormatter formatter =
                            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
                    servicoEmail.enviarEmailTexto(
                            email,
                            "Perícia Médica Necessária – Solicitação de Medicação",
                            """
                            Prezado(a) Sr(a). %s,
        
                            Informamos que sua solicitação de medicação encontra-se em análise
                            e foi identificada a necessidade de realização de perícia médica.
        
                            O agendamento da perícia foi realizado para dia %s.
        
                            Esta mensagem é automática e enviada pelo sistema de gerenciamento
                            de solicitações de medicação.
        
                            Atenciosamente,
                            Sistema GMAC
                            """.formatted(nomePaciente, data.format(formatter)).toUpperCase(Locale.forLanguageTag("pt-BR")));

                    doc.setStatusDocumentacao(StatusDocumentacao.PERICIA);
                    doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.PENDENTE);

                    PericiaDTO periciaDTO = new PericiaDTO(
                            data,
                            StatusPericia.AGENDADA,
                            doc.getPaciente(),
                            usuario,
                            doc
                    );
                    servicoMarcarPericia.marcarPericia(periciaDTO);
                }
            }
        }

    }
