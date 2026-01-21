package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusPericia;

public record PaginaPericiaDTO(String id, String nomePaciente, String nomeMedico, StatusPericia statusPericia, String data) {
}
