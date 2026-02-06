package com.example.apigmac.servicos.usuariosServicos;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.*;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.*;
import com.example.apigmac.utils.CpfUtils;
import com.example.apigmac.utils.ServicoVerificacao;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo registro de usuários e criação
 * das entidades específicas conforme o perfil.
 */
@Service
public class ServicoRegistro {

    @Autowired
    private ServicoVerificacao verificacao;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioAdm repositorioAdm;

    @Autowired
    private RepositorioMed repositorioMed;

    @Autowired
    private RepositorioRecepicionista repositorioRecepicionista;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Cadastra um novo usuário validando regras de negócio e unicidade.
     */
    @Transactional
    public Usuario cadastrarUsuario(RegistroUsuarioDTO dados) {

        validarDadosObrigatorios(dados);

        String cpfNormalizado = CpfUtils.normalizar(dados.cpf());

        validarCpf(cpfNormalizado);
        validarEmail(dados.email());
        validarLogin(dados.login());
        validarPerfil(dados);

        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        Usuario usuario = new Usuario(
                dados.login(),
                dados.email(),
                senhaCriptografada,
                cpfNormalizado,
                dados.nome(),
                dados.perfil(),
                dados.dataNascimento()
        );

        usuario = repositorioUsuario.save(usuario);
        criarPerfilEspecifico(usuario, dados);

        return usuario;
    }

    private void validarDadosObrigatorios(RegistroUsuarioDTO dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados não informados");
        }

        if (!verificacao.textoObrigatorioValido(dados.nome(), 3)) {
            throw new IllegalArgumentException("Nome inválido");
        }

        if (!verificacao.senhaValida(dados.senha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        if (!verificacao.dataNascimentoValida(dados.dataNascimento())) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }
    }

    private void validarCpf(String cpf) {
        if (!verificacao.cpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }

        if (repositorioUsuario.findByCpf(cpf) != null) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }

    private void validarEmail(String email) {
        if (!verificacao.emailValido(email)) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (repositorioUsuario.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
    }

    private void validarLogin(String login) {
        if (repositorioUsuario.findByLogin(login) != null) {
            throw new IllegalArgumentException("Login já existente");
        }
    }

    private void validarPerfil(RegistroUsuarioDTO dados) {
        if (dados.perfil() == null) {
            throw new IllegalArgumentException("Perfil é obrigatório");
        }

        if (dados.perfil() == Perfil.MEDICO &&
                !verificacao.textoObrigatorioValido(dados.especializacao(), 3)) {
            throw new IllegalArgumentException("Especialização é obrigatória para médico");
        }
    }

    private void criarPerfilEspecifico(Usuario usuario, RegistroUsuarioDTO dados) {

        switch (dados.perfil()) {
            case ADMINISTRADOR -> repositorioAdm.save(new Administrador(usuario));
            case MEDICO -> repositorioMed.save(new Medico(usuario, dados.especializacao()));
            case RECEPCIONISTA -> repositorioRecepicionista.save(new Recepcionista(usuario));
            default -> throw new IllegalArgumentException("Perfil inválido");
        }
    }
}
