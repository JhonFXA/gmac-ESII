package com.example.apigmac.servicos;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.administrador.Administrador;
import com.example.apigmac.entidades.medico.Medico;
import com.example.apigmac.entidades.recepcionista.Recepcionista;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioAdm;
import com.example.apigmac.repositorios.RepositorioMed;
import com.example.apigmac.repositorios.RepositorioRecepicionista;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.time.LocalDate;

import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.Objects;
@Service
public class ServicoRegistro {

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

        if (!cpfValido(dados.cpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }

        if (!senhaValida(dados.senha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        if (!emailValido(dados.email())) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (!dataNascimentoValida(dados.dataNascimento())) {
            throw new IllegalArgumentException("Data de nascimento inválida");
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

    private boolean cpfValido(String cpf) {
        if (Objects.isNull(cpf)) {
            return false;
        }

        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        if (cpfLimpo.length() != 11) {
            return false;
        }

        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int[] digitos = new int[11];
            for (int i = 0; i < 11; i++) {
                digitos[i] = Character.getNumericValue(cpfLimpo.charAt(i));
            }

            int soma1 = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                soma1 += digitos[i] * peso--;
            }

            int dv1Calculado = 11 - (soma1 % 11);
            if (dv1Calculado > 9) {
                dv1Calculado = 0;
            }

            if (dv1Calculado != digitos[9]) {
                return false;
            }

            int soma2 = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                soma2 += digitos[i] * peso--;
            }

            int dv2Calculado = 11 - (soma2 % 11);
            if (dv2Calculado > 9) {
                dv2Calculado = 0;
            }

            if (dv2Calculado != digitos[10]) {
                return false;
            }

        } catch (InputMismatchException e) {

            return false;
        }

        return true;
    }

    private boolean emailValido(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }

        String domain = email.substring(email.indexOf("@") + 1);

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ctx = new InitialDirContext(env);

            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            if (attr == null) {
                attrs = ctx.getAttributes(domain, new String[]{"A"});
                attr = attrs.get("A");
            }

            return attr != null && attr.size() > 0;

        } catch (NamingException e) {
            return false;
        }
    }

    private boolean senhaValida(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$";
        if (!senha.matches(regex)) {
            return false;
        }

        for (int i = 0; i < senha.length() - 2; i++) {
            char c1 = senha.charAt(i);
            char c2 = senha.charAt(i + 1);
            char c3 = senha.charAt(i + 2);

            if (Character.isDigit(c1) && Character.isDigit(c2) && Character.isDigit(c3)) {
                if ((c2 == c1 + 1) && (c3 == c2 + 1)) {
                    return false;
                }
            }

            if (Character.isLetter(c1) && Character.isLetter(c2) && Character.isLetter(c3)) {
                if ((c2 == c1 + 1) && (c3 == c2 + 1)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean dataNascimentoValida(LocalDate dataNascimento) {
        if (Objects.isNull(dataNascimento)) {
            return false;
        }

        if (!dataNascimento.isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }
}
