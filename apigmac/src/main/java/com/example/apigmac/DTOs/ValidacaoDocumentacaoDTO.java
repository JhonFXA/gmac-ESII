package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusValidacaoDocumentacao;

import java.time.LocalDate;
import java.util.UUID;

public record ValidacaoDocumentacaoDTO (
   String login,
    String senha,
    UUID documentacaoId,
    String observacao,
    StatusValidacaoDocumentacao status,
    LocalDate data
)
{}
