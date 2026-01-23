package com.example.apigmac.utils;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PacienteRelatorioSpecs {

    /**
     * Pacientes Beneficiados:
     * Pelo menos uma documentação aprovada no período.
     */
    public static Specification<Paciente> beneficiados(LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> {
            // Criamos uma subquery: "Existe alguma documentação aprovada para este paciente?"
            Subquery<Integer> subquery = query.subquery(Integer.class);
            var subRoot = subquery.from(Documentacao.class);
            subquery.select(cb.literal(1)); // Não importa o valor, apenas se existe

            subquery.where(
                    cb.equal(subRoot.get("paciente"), root),
                    cb.equal(subRoot.get("statusDocumentacao"), StatusDocumentacao.APROVADA),
                    cb.between(subRoot.get("dataEnvio"), inicio, fim)
            );

            return cb.exists(subquery);
        };
    }

    /**
     * Pacientes Não Beneficiados:
     * StatusSolicitacao = FINALIZADA E nenhuma documentação aprovada no período.
     */
    public static Specification<Paciente> naoBeneficiados(LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> {
            // Subquery para verificar se existe ALGUMA aprovada
            Subquery<Long> subquery = query.subquery(Long.class);
            var subRoot = subquery.from(Documentacao.class);
            subquery.select(cb.count(subRoot));
            subquery.where(
                    cb.equal(subRoot.get("paciente"), root),
                    cb.equal(subRoot.get("statusDocumentacao"), StatusDocumentacao.APROVADA),
                    cb.between(subRoot.get("dataEnvio"), inicio, fim)
            );

            return cb.and(
                    cb.equal(root.get("statusSolicitacao"), StatusSolicitacao.FINALIZADA),
                    cb.equal(subquery, 0L) // Onde a contagem de aprovadas é zero
            );
        };
    }


    public static Specification<Paciente> pendentes() {
        return (root, query, cb) ->
                cb.equal(root.get("statusSolicitacao"), StatusSolicitacao.PENDENTE);
    }
}