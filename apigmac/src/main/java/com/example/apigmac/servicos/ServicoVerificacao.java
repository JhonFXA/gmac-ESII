package com.example.apigmac.servicos;

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
public class ServicoVerificacao {

    public boolean cpfValido(String cpf) {
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

    public boolean emailValido(String email) {
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

    public boolean senhaValida(String senha) {
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

    public boolean dataNascimentoValida(LocalDate dataNascimento) {
        if (Objects.isNull(dataNascimento)) {
            return false;
        }

        if (!dataNascimento.isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }
}
