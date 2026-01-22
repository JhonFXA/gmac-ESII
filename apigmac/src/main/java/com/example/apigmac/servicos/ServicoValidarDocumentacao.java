package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.entidades.ValidacaoDocumentacao;
import com.example.apigmac.modelo.enums.*;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioValidacaoDocumentacao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ServicoValidarDocumentacao {

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

            // 3. Validação de Regras de Perfil vs Ação
            validarPermissao(usuario, dto.status());

            // 4. Criação do registro de validação
            ValidacaoDocumentacao validacao = new ValidacaoDocumentacao();
            validacao.setDocumentacao(documentacao);
            validacao.setPaciente(documentacao.getPaciente());
            validacao.setUsuario(usuario);
            validacao.setDataValidacao(LocalDate.now());
            validacao.setObservacao(dto.observacao());
            validacao.setStatusValidacaoDocumentacao(dto.status());
            System.out.println(dto.status());

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
        boolean ehMedico = usuario.getPerfil() == Perfil.MEDICO;

        if (!ehMedico) {
            if (status != StatusValidacaoDocumentacao.PERICIA) {
                throw new AccessDeniedException(
                        "Apenas médicos podem APROVAR ou REPROVAR documentos. Seu perfil permite apenas solicitar PERÍCIA."
                );
            }
        }
    }

    private void atualizarStatusRelacionados(Documentacao doc, StatusValidacaoDocumentacao status, Usuario usuario, Date data) {
        switch (status) {
            case APROVADA:
                doc.setStatusDocumentacao(StatusDocumentacao.APROVADA);
                doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                break;
            case REPROVADA:
                doc.setStatusDocumentacao(StatusDocumentacao.REPROVADA);
                doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                break;
            case PERICIA:

                doc.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
                doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.PENDENTE);
                PericiaDTO periciaDTO = new PericiaDTO(
                        data,
                        StatusPericia.AGENDADA,
                        doc.getPaciente(),
                        usuario,
                        doc
                );
                servicoMarcarPericia.marcarPericia(periciaDTO);
                break;
        }
    }
}
