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

    // Injeção por construtor para garantir imutabilidade e facilitar testes
    private final ServicoGerarRelatorio servicoGerarRelatorio;

    /**
     * Endpoint responsável por centralizar os dados do dashboard,
     * agregando diferentes relatórios em uma única resposta.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardRelatorioDTO> obterDashboard(
            @RequestParam int ano,
            @RequestParam TipoPeriodo tipo,
            @RequestParam int valor
    ) {
        // Geração do relatório de benefícios conforme o período informado
        RelatorioBeneficioDTO beneficio =
                servicoGerarRelatorio.gerarRelatorioBeneficio(ano, tipo, valor);

        // Geração do relatório de documentação utilizando os mesmos critérios temporais
        RelatorioDocumentacaoDTO documentacao =
                servicoGerarRelatorio.gerarRelatorioDocumentacao(ano, tipo, valor);

        // Consolidação dos relatórios em um DTO único para consumo pelo dashboard
        return ResponseEntity.ok(
                new DashboardRelatorioDTO(beneficio, documentacao)
        );
    }
}
