package com.example.apigmac.DTOs;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.StatusPericia;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record PericiaDTO(
        @FutureOrPresent(message = "A data da perícia deve ser hoje ou uma data futura")
        Date dataPericia,
        StatusPericia statusPericia,
        Paciente paciente,
        Usuario usuario,
        Documentacao documentacao
){
}
