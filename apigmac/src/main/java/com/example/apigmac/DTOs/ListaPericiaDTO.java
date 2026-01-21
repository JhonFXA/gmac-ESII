package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusPericia;


public record ListaPericiaDTO(String nomePaciente, String nomeMedico, StatusPericia statusPericia,boolean decrescente,int pagina,int tamanho) {
}
