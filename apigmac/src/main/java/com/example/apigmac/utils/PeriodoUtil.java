package com.example.apigmac.utils;

import com.example.apigmac.modelo.enums.TipoPeriodo;

import java.time.LocalDate;

public class PeriodoUtil {

    public static LocalDate inicio(int ano, TipoPeriodo tipo, int valor) {
        return switch (tipo) {
            case MES -> LocalDate.of(ano, valor, 1);
            case TRIMESTRE -> LocalDate.of(ano, (valor - 1) * 3 + 1, 1);
            case SEMESTRE -> LocalDate.of(ano, valor == 1 ? 1 : 7, 1);
            case ANO -> LocalDate.of(ano, 1, 1);
        };
    }

    public static LocalDate fim(int ano, TipoPeriodo tipo, int valor) {
        return switch (tipo) {
            case MES -> inicio(ano, tipo, valor).withDayOfMonth(
                    inicio(ano, tipo, valor).lengthOfMonth()
            );
            case TRIMESTRE -> inicio(ano, tipo, valor).plusMonths(3).minusDays(1);
            case SEMESTRE -> inicio(ano, tipo, valor).plusMonths(6).minusDays(1);
            case ANO -> LocalDate.of(ano, 12, 31);
        };
    }
}
