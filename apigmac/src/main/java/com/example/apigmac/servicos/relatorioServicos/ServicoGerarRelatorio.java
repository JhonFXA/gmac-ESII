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

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    public RelatorioDocumentacaoDTO gerarRelatorioDocumentacao(int ano, TipoPeriodo tipoPeriodo, int valor){

        LocalDate inicio = PeriodoUtil.inicio(ano, tipoPeriodo, valor);
        LocalDate fim = PeriodoUtil.fim(ano, tipoPeriodo, valor);

        Specification<Documentacao> specPendente = DocumentacaoRelatorioSpecs.porStatusEPeriodo(StatusDocumentacao.PENDENTE, inicio, fim);
        long qtdPendente = repositorioDocumentacao.count(specPendente);

        Specification<Documentacao> specAprovada = DocumentacaoRelatorioSpecs.porStatusEPeriodo(StatusDocumentacao.APROVADA, inicio, fim);
        long qtdAprovada = repositorioDocumentacao.count(specAprovada);

        Specification<Documentacao> specReprovada = DocumentacaoRelatorioSpecs.porStatusEPeriodo(StatusDocumentacao.REPROVADA, inicio, fim);
        long qtdReprovada = repositorioDocumentacao.count(specReprovada);

        Specification<Documentacao> specTotal = DocumentacaoRelatorioSpecs.entreDatas(inicio, fim);

        long qtdTotal = repositorioDocumentacao.count(specTotal);

        return new RelatorioDocumentacaoDTO(qtdTotal,qtdAprovada,qtdReprovada,qtdPendente);
    }

    public RelatorioBeneficioDTO gerarRelatorioBeneficio(int ano, TipoPeriodo tipoPeriodo, int valor){

        LocalDate inicio = PeriodoUtil.inicio(ano, tipoPeriodo, valor);
        LocalDate fim = PeriodoUtil.fim(ano, tipoPeriodo, valor);

        long totalBeneficiados = repositorioPaciente.count(PacienteRelatorioSpecs.beneficiados(inicio, fim));

        long totalNaoBeneficiados = repositorioPaciente.count(PacienteRelatorioSpecs.naoBeneficiados(inicio, fim));

        long totalPendentes = repositorioPaciente.count(PacienteRelatorioSpecs.pendentes());

        long qtdTotal = repositorioPaciente.count();

        return new RelatorioBeneficioDTO(qtdTotal,totalBeneficiados,totalNaoBeneficiados,totalPendentes);
    }
}
