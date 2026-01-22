package com.example.apigmac.DTOs;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record PericiaDTO(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime dataPericia,
        StatusPericia statusPericia,
        Paciente paciente,
        Usuario usuario,
        Documentacao documentacao
){
}
