package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusSolicitacao;

import java.time.LocalDate;

public record PaginaPacienteDTO (String nome, String cpf, StatusSolicitacao statusSolicitacao, LocalDate dataNascimento){
}
