package com.example.apigmac.servicos.relatorioServicos;

import com.example.apigmac.DTOs.RelatorioBeneficioDTO;
import com.example.apigmac.DTOs.RelatorioDocumentacaoDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.TipoPeriodo;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.utils.DocumentacaoRelatorioSpecs;
import com.example.apigmac.utils.PacienteRelatorioSpecs;
import com.example.apigmac.utils.PeriodoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
@Service
public class ServicoGerarRelatorio {

    // Repositório utilizado para consultas estatísticas relacionadas às documentações
    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    // Repositório utilizado para consolidação de dados estatísticos dos pacientes
    @Autowired
    private RepositorioPaciente repositorioPaciente;

    /**
     * Gera o relatório de documentação com base em período dinâmico,
     * permitindo análise consolidada por status.
     */
    public RelatorioDocumentacaoDTO gerarRelatorioDocumentacao(
            int ano,
            TipoPeriodo tipoPeriodo,
            int valor){

        // Define o intervalo de datas a partir do tipo de período solicitado
        LocalDate inicio = PeriodoUtil.inicio(ano, tipoPeriodo, valor);
        LocalDate fim = PeriodoUtil.fim(ano, tipoPeriodo, valor);

        // Contabiliza documentações pendentes no período
        Specification<Documentacao> specPendente =
                DocumentacaoRelatorioSpecs.porStatusEPeriodo(
                        StatusDocumentacao.PENDENTE, inicio, fim);
        long qtdPendente = repositorioDocumentacao.count(specPendente);

        // Contabiliza documentações aprovadas no período
        Specification<Documentacao> specAprovada =
                DocumentacaoRelatorioSpecs.porStatusEPeriodo(
                        StatusDocumentacao.APROVADA, inicio, fim);
        long qtdAprovada = repositorioDocumentacao.count(specAprovada);

        // Contabiliza documentações reprovadas no período
        Specification<Documentacao> specReprovada =
                DocumentacaoRelatorioSpecs.porStatusEPeriodo(
                        StatusDocumentacao.REPROVADA, inicio, fim);
        long qtdReprovada = repositorioDocumentacao.count(specReprovada);

        // Obtém o total de documentações cadastradas no intervalo informado
        Specification<Documentacao> specTotal =
                DocumentacaoRelatorioSpecs.entreDatas(inicio, fim);
        long qtdTotal = repositorioDocumentacao.count(specTotal);

        // Retorna o DTO consolidando os dados para uso em dashboards e relatórios
        return new RelatorioDocumentacaoDTO(
                qtdTotal,
                qtdAprovada,
                qtdReprovada,
                qtdPendente
        );
    }

    /**
     * Gera o relatório de benefícios considerando o status das solicitações
     * dos pacientes dentro de um período específico.
     */
    public RelatorioBeneficioDTO gerarRelatorioBeneficio(
            int ano,
            TipoPeriodo tipoPeriodo,
            int valor){

        // Define o intervalo temporal a ser utilizado nos filtros
        LocalDate inicio = PeriodoUtil.inicio(ano, tipoPeriodo, valor);
        LocalDate fim = PeriodoUtil.fim(ano, tipoPeriodo, valor);

        // Quantifica pacientes que tiveram benefício concedido no período
        long totalBeneficiados =
                repositorioPaciente.count(
                        PacienteRelatorioSpecs.beneficiados(inicio, fim));

        // Quantifica pacientes que não obtiveram benefício no período
        long totalNaoBeneficiados =
                repositorioPaciente.count(
                        PacienteRelatorioSpecs.naoBeneficiados(inicio, fim));

        // Quantifica solicitações ainda pendentes no período
        long totalPendentes =
                repositorioPaciente.count(
                        PacienteRelatorioSpecs.pendentes(inicio, fim));

        // Quantifica o total de pacientes cadastrados no período analisado
        long qtdTotal =
                repositorioPaciente.count(
                        PacienteRelatorioSpecs.cadastradoNoPeriodo(inicio, fim));

        // Retorna o DTO consolidado para visualização em relatórios gerenciais
        return new RelatorioBeneficioDTO(
                qtdTotal,
                totalBeneficiados,
                totalNaoBeneficiados,
                totalPendentes
        );
    }
}
