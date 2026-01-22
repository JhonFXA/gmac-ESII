package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusValidacaoDocumentacao;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record ValidacaoDocumentacaoDTO (
    String login,
    String senha,
    UUID documentacaoId,
    String observacao,
    StatusValidacaoDocumentacao status,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime data
)
{}
