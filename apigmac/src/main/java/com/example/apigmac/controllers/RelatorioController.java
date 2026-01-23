package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.DashboardRelatorioDTO;
import com.example.apigmac.DTOs.RelatorioBeneficioDTO;
import com.example.apigmac.DTOs.RelatorioDocumentacaoDTO;
import com.example.apigmac.modelo.enums.TipoPeriodo;
import com.example.apigmac.servicos.relatorioServicos.ServicoGerarRelatorio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("relatorio")
@RequiredArgsConstructor
public class RelatorioController {

    private final ServicoGerarRelatorio servicoGerarRelatorio;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardRelatorioDTO> obterDashboard(
            @RequestParam int ano,
            @RequestParam TipoPeriodo tipo,
            @RequestParam int valor
    ) {
        RelatorioBeneficioDTO beneficio = servicoGerarRelatorio.gerarRelatorioBeneficio(ano, tipo, valor);
        RelatorioDocumentacaoDTO documentacao = servicoGerarRelatorio.gerarRelatorioDocumentacao(ano, tipo, valor);

        return ResponseEntity.ok(new DashboardRelatorioDTO(beneficio, documentacao));
    }
}