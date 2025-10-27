package com.example.apigmac.modelo.enums;

public enum Perfil {
    ADMINISTRADOR("administrador"),
    RECEPCIONISTA("recepcionista"),
    MEDICO("medico");

    private String perfil;

    Perfil (String perfil){
        this.perfil = perfil;
    }

    public String getPerfil (){
        return  perfil;
    }
}
