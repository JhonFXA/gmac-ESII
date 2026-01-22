package com.example.apigmac.modelo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusSolicitacao {
    PENDENTE("Pendente"),
    FINALIZADA("Finalizada");

    private final String descricao;
}