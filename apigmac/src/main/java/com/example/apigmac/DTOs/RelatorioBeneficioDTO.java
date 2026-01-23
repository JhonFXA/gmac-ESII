package com.example.apigmac.DTOs;

public record RelatorioBeneficioDTO(
        long totalPacientes,
        long pacientesBeneficiados,
        long pacientesNaoBeneficiados,
        long pacientesPendentes
) {}