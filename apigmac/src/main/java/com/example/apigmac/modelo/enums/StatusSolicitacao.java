package com.example.apigmac.modelo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusSolicitacao {
    PENDENTE("Pendente"),
    ANALISE("Em Análise"),
    APROVADA("Aprovada"),
    NEGADA("Negada"),
    FINALIZADA("Finalizada");

    private final String descricao;
}