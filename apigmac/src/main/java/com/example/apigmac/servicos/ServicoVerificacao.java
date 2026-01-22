package com.example.apigmac.servicos;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.NamingException;
import javax.naming.directory.*;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Objects;

@Service
public class ServicoVerificacao {

    /* =========================
       CPF
       ========================= */

    public boolean cpfValido(String cpf) {
        if (cpf == null) return false;

        // 2. Remove a formatação para realizar o cálculo dos dígitos verificadores
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");

        // 3. Verifica se todos os dígitos são iguais (ex: 111.111.111-11), o que é inválido
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int[] d = cpfLimpo.chars().map(c -> c - '0').toArray();

            // Cálculo do 1º Dígito Verificador
            int soma1 = 0;
            for (int i = 0; i < 9; i++) soma1 += d[i] * (10 - i);
            int dv1 = 11 - (soma1 % 11);
            if (dv1 > 9) dv1 = 0;

            // Cálculo do 2º Dígito Verificador
            int soma2 = 0;
            for (int i = 0; i < 10; i++) soma2 += d[i] * (11 - i);
            int dv2 = 11 - (soma2 % 11);
            if (dv2 > 9) dv2 = 0;

            // Retorna verdadeiro se os dígitos calculados batem com os informados
            return dv1 == d[9] && dv2 == d[10];

        } catch (Exception e) {
            return false;
        }
    }
    /* =========================
       EMAIL
       ========================= */

    public boolean emailValido(String email) {
        if (email == null || !email.contains("@")) return false;

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

    /* =========================
       TELEFONE
       ========================= */

    public boolean telefoneValido(String telefone) {
        if (telefone == null) return false;

        String limpo = telefone.replaceAll("[^0-9]", "");
        return limpo.length() == 10 || limpo.length() == 11;
    }

    /* =========================
       DATA
       ========================= */

    public boolean dataNascimentoValida(LocalDate data) {
        return data != null && data.isBefore(LocalDate.now());
    }

    /* =========================
       TEXTO
       ========================= */

    public boolean textoObrigatorioValido(String texto, int tamanhoMinimo) {
        return texto != null && texto.trim().length() >= tamanhoMinimo;
    }

    /* =========================
       UF / ESTADO
       ========================= */

    public boolean estadoValido(String estado) {
        return estado != null && estado.matches("[A-Z]{2}");
    }

    /* =========================
       CEP
       ========================= */

    public boolean cepValido(String cep) {
        if (cep == null) return false;
        // Aceita 12345678 ou 12345-678
        return cep.matches("\\d{8}") || cep.matches("\\d{5}-\\d{3}");
    }

    /* =========================
       PDF / DOCUMENTO
       ========================= */

    public boolean pdfValido(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            return false;
        }

        return Objects.requireNonNull(file.getOriginalFilename())
                .toLowerCase()
                .endsWith(".pdf");
    }
    public boolean senhaValida(String senha) {
        if (senha == null || senha.length() < 8) {
            return false;
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$";
        if (!senha.matches(regex)) {
            return false;
        }

        return true;
    }
}
