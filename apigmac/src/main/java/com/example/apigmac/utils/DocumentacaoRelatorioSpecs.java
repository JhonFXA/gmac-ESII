package com.example.apigmac.utils;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentacaoRelatorioSpecs {

    public static Specification<Documentacao> entreDatas(LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> cb.between(root.get("dataEnvio"), inicio, fim);
    }

    public static Specification<Documentacao> porStatusEPeriodo(StatusDocumentacao status, LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("statusDocumentacao"), status));
            }
            if (inicio != null && fim != null) {
                predicates.add(cb.between(root.get("dataEnvio"), inicio, fim));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}