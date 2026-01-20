package com.example.apigmac.DTOs;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.StatusPericia;

import java.time.LocalDate;
import java.util.UUID;

public record PericiaDTO(
        LocalDate dataPericia,
        StatusPericia statusPericia,
        UUID paciente,
        UUID usuario,
        UUID documentacao
){
}
