package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusValidacaoDocumentacao;

import java.time.LocalDate;
import java.util.UUID;

public record ValidacaoPericiaDTO (
        UUID periciaId,
        ValidacaoDocumentacaoDTO validacaoDocumentacaoDTO
)
{}