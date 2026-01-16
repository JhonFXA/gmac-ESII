package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.Sexo;
import com.example.apigmac.modelo.enums.StatusSolicitacao;

import java.time.LocalDate;
import java.util.UUID;

public record AlterarPacienteDTO(
        UUID id,
        String cpf,
        String nome,
        String telefone,
        String email,
        Sexo sexo,
        EstadoCivil estadoCivil,
        StatusSolicitacao statusSolicitacao,
        LocalDate dataNascimento
) {}

