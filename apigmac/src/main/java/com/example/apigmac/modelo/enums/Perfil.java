package com.example.apigmac.modelo.enums;

import lombok.Getter;

@Getter
public enum Perfil {
    ADMINISTRADOR("administrador"),
    RECEPCIONISTA("recepcionista"),
    MEDICO("medico"),
    INATIVO("inativo");

    private final String perfil;

    Perfil (String perfil){
        this.perfil = perfil;
    }

}
