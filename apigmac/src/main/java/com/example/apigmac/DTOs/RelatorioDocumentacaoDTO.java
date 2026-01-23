package com.example.apigmac.DTOs;

public record RelatorioDocumentacaoDTO(
        long totalDocumentacoes,
        long aprovadas,
        long reprovadas,
        long pendentes
) {}