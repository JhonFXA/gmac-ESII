package com.example.apigmac.DTOs;

public record DashboardRelatorioDTO(
        RelatorioBeneficioDTO resumoBeneficios,
        RelatorioDocumentacaoDTO resumoDocumentacoes
) {}