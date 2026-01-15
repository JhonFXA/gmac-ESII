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

    public Usuario cadastrarUsuario(RegistroUsuarioDTO dados) {
        if (repositorioUsuario.findByLogin(dados.login()) != null) {
            throw new IllegalArgumentException("Login já existe");
        }

        if (repositorioUsuario.findByCpf(dados.cpf()) != null){
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (!verificacao.cpfValido(dados.cpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }
        if (!verificacao.senhaValida(dados.senha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        if (repositorioUsuario.findByEmail(dados.email()) != null){
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (!verificacao.emailValido(dados.email())) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (!verificacao.dataNascimentoValida(dados.dataNascimento())) {
            throw new IllegalArgumentException("Data inválida");
        }

        String senhaCriptografada = passwordEncoder.encode(dados.senha());

        Usuario usuario = new Usuario(
                dados.login(),
                dados.email(),
                senhaCriptografada,
                dados.cpf(),
                dados.nome(),
                dados.perfil(),
                dados.dataNascimento()
        );


        if (dados.perfil() == Perfil.ADMINISTRADOR) {
            Administrador admin = new Administrador(usuario.getId(), usuario);
            repositorioAdm.save(admin);
        } else if (dados.perfil() == Perfil.MEDICO) {
            Medico med = new Medico(usuario.getId(), usuario, "MEDICO");
            repositorioMed.save(med);
        } else if (dados.perfil() == Perfil.RECEPCIONISTA) {
            Recepcionista recep = new Recepcionista(usuario.getId(), usuario);
            repositorioRecepicionista.save(recep);
        }

        usuario = repositorioUsuario.saveAndFlush(usuario);
        return usuario;
    }

}
