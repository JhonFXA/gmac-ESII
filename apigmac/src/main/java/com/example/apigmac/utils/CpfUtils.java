package com.example.apigmac.utils;

public final class CpfUtils {

    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("\\D", "");
    }
    public static String formatar(String cpf) {
        return cpf.replaceAll(
                "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4"
        );
    }

}

