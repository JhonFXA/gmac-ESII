package com.example.apigmac.utils;

import com.example.apigmac.entidades.Cadastro;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PacienteRelatorioSpecs {

    public static Specification<Paciente> cadastradoNoPeriodo(
            LocalDate inicio,
            LocalDate fim
    ) {
        return (root, query, cb) -> {
            Subquery<Integer> subquery = query.subquery(Integer.class);
            var cadastroRoot = subquery.from(Cadastro.class);

            subquery.select(cb.literal(1));
            subquery.where(
                    cb.equal(cadastroRoot.get("paciente"), root),
                    cb.between(cadastroRoot.get("dataCadastro"), inicio, fim)
            );

            return cb.exists(subquery);
        };
    }


    /**
     * Pacientes Beneficiados:
     * Pelo menos uma documentação aprovada no período.
     */
    public static Specification<Paciente> beneficiados(
            LocalDate inicio,
            LocalDate fim
    ) {
        return (root, query, cb) -> {

            // Subquery: existe documentação aprovada no período
            Subquery<Integer> docSubquery = query.subquery(Integer.class);
            var docRoot = docSubquery.from(Documentacao.class);

            docSubquery.select(cb.literal(1));
            docSubquery.where(
                    cb.equal(docRoot.get("paciente"), root),
                    cb.equal(docRoot.get("statusDocumentacao"), StatusDocumentacao.APROVADA),
                    cb.between(docRoot.get("dataEnvio"), inicio, fim)
            );

            return cb.and(
                    cb.exists(docSubquery),
                    cadastradoNoPeriodo(inicio, fim).toPredicate(root, query, cb)
            );
        };
    }

    /**
     * Pacientes Não Beneficiados:
     * StatusSolicitacao = FINALIZADA E nenhuma documentação aprovada no período.
     */
    public static Specification<Paciente> naoBeneficiados(
            LocalDate inicio,
            LocalDate fim
    ) {
        return (root, query, cb) -> {

            // Subquery para contar documentações aprovadas
            Subquery<Long> subquery = query.subquery(Long.class);
            var docRoot = subquery.from(Documentacao.class);

            subquery.select(cb.count(docRoot));
            subquery.where(
                    cb.equal(docRoot.get("paciente"), root),
                    cb.equal(docRoot.get("statusDocumentacao"), StatusDocumentacao.APROVADA),
                    cb.between(docRoot.get("dataEnvio"), inicio, fim)
            );

            return cb.and(
                    cb.equal(root.get("statusSolicitacao"), StatusSolicitacao.FINALIZADA),
                    cb.equal(subquery, 0L),
                    cadastradoNoPeriodo(inicio, fim).toPredicate(root, query, cb)
            );
        };
    }


    public static Specification<Paciente> pendentes(
            LocalDate inicio,
            LocalDate fim
    ) {
        return (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("statusSolicitacao"), StatusSolicitacao.PENDENTE),
                        cadastradoNoPeriodo(inicio, fim).toPredicate(root, query, cb)
                );
    }
}