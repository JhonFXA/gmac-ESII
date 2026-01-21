package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusValidacaoDocumentacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record ValidacaoDocumentacaoDTO (
   String login,
    String senha,
    UUID documentacaoId,
    String observacao,
    StatusValidacaoDocumentacao status,
    Date data
)
{}
