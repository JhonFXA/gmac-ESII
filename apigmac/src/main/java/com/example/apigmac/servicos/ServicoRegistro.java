package com.example.apigmac.servicos;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.Administrador;
import com.example.apigmac.entidades.Medico;
import com.example.apigmac.entidades.Recepcionista;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioAdm;
import com.example.apigmac.repositorios.RepositorioMed;
import com.example.apigmac.repositorios.RepositorioRecepicionista;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.CpfUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Transactional
    public Usuario cadastrarUsuario(RegistroUsuarioDTO dados) {

        if (dados == null) {
            throw new IllegalArgumentException("Dados não informados");
        }
        String cpfNormalizado = CpfUtils.normalizar((dados.cpf()));

        if (!verificacao.textoObrigatorioValido(dados.nome(), 3)) {
            throw new IllegalArgumentException("Nome inválido");
        }

        if (!verificacao.cpfValido(cpfNormalizado)) {
            throw new IllegalArgumentException("CPF inválido");
        }

        if (repositorioUsuario.findByCpf(cpfNormalizado) != null) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (!verificacao.emailValido(dados.email())) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (repositorioUsuario.findByEmail(dados.email()) != null) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (repositorioUsuario.findByLogin(dados.login()) != null) {
            throw new IllegalArgumentException("Login já existe");
        }

        if (!verificacao.senhaValida(dados.senha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        if (!verificacao.dataNascimentoValida(dados.dataNascimento())) {
            throw new IllegalArgumentException("Data inválida");
        }

        if (dados.perfil() == null) {
            throw new IllegalArgumentException("Perfil é obrigatório");
        }

        if (dados.perfil() == Perfil.MEDICO) {

            if (!verificacao.textoObrigatorioValido(dados.especializacao(), 3)) {
                throw new IllegalArgumentException("Especialização é obrigatória para médico");
            }
        }


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

        switch (dados.perfil()) {

            case ADMINISTRADOR -> {
                Administrador admin = new Administrador(usuario);
                repositorioAdm.save(admin);
            }

            case MEDICO -> {
                Medico med = new Medico(usuario,dados.especializacao());
                repositorioMed.save(med);
            }

            case RECEPCIONISTA -> {
                Recepcionista recep = new Recepcionista(usuario);
                repositorioRecepicionista.save(recep);
            }

            default -> throw new IllegalArgumentException("Perfil inválido");
        }

        return usuario;
    }
}
