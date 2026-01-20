package com.example.apigmac.servicos;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.entidades.ValidacaoDocumentacao;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.modelo.enums.StatusValidacaoDocumentacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.repositorios.RepositorioValidacaoDocumentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class ServicoValidarDocumentacao {

    @Autowired
    private ServicoBuscarUsuario servicoBuscarUsuario;

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RepositorioValidacaoDocumentacao repositorioValidacaoDocumentacao;

    public void registrarValidacao(String login, String senha, UUID id, String observacao, StatusValidacaoDocumentacao statusEscolhido, Date data) {
        // 1. Autenticação (Re-autenticação por segurança)
        var authToken = new UsernamePasswordAuthenticationToken(login, senha);
        var auth = authenticationManager.authenticate(authToken);
        Usuario usuario = (Usuario) auth.getPrincipal();

        // 2. Busca a documentação
        Optional<Documentacao> documentacao = repositorioDocumentacao.findById(id);
        if (documentacao.isEmpty()) {
            throw new RuntimeException("Documentação não encontrada.");
        }

        // 3. Validação de Regras de Perfil vs Ação
        validarPermissao(usuario, statusEscolhido);

        // 4. Criação do registro de validação
        ValidacaoDocumentacao validacao = new ValidacaoDocumentacao();
        validacao.setDocumentacao(documentacao.get());
        validacao.setPaciente(documentacao.get().getPaciente());
        validacao.setUsuario(usuario);
        validacao.setDataValidacao(LocalDate.now());
        validacao.setObservacao(observacao);
        validacao.setStatusValidacaoDocumentacao(statusEscolhido);

        // 5. Atualização dos Status conforme a escolha
        atualizarStatusRelacionados(documentacao.orElse(null), statusEscolhido);

        // 6. Persistência
        repositorioValidacaoDocumentacao.save(validacao);
        repositorioDocumentacao.save(documentacao.get()); // Garante a atualização do status da doc e paciente
    }

    private void validarPermissao(Usuario usuario, StatusValidacaoDocumentacao status) {
        boolean ehMedico = usuario.getPerfil() == Perfil.MEDICO;

        if (!ehMedico) {
            // Se não for médico, a ÚNICA coisa que pode fazer é PERICIA
            if (status != StatusValidacaoDocumentacao.PERICIA) {
                throw new RuntimeException("Apenas médicos podem APROVAR ou REPROVAR documentos. Seu perfil permite apenas solicitar PERÍCIA.");
            }
        }
    }

    private void atualizarStatusRelacionados(Documentacao doc, StatusValidacaoDocumentacao status) {
        switch (status) {
            case APROVADO:
                doc.setStatusDocumentacao(StatusDocumentacao.APROVADA);
                doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                break;
            case REPROVADO:
                doc.setStatusDocumentacao(StatusDocumentacao.REPROVADA);
                doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.FINALIZADA);
                break;
            case PERICIA:
                doc.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
                doc.getPaciente().setStatusSolicitacao(StatusSolicitacao.PENDENTE);
                break;
        }
    }
}
