package com.example.apigmac.utils;

import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.modelo.enums.StatusPericia;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PericiaSpecs {

    public static Specification<Pericia> filtrar(String nomePaciente, String nomeMedico, StatusPericia statusPericia) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            assert query != null;
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("paciente", JoinType.LEFT);
                root.fetch("usuario", JoinType.LEFT);
                query.distinct(true);
            }

            // 2. Filtros (Usando join para garantir a filtragem correta)
            if (nomePaciente != null && !nomePaciente.isEmpty()) {
                // cb.lower(root.join("paciente").get("nome")) é o caminho correto
                predicates.add(cb.like(
                        cb.lower(root.join("paciente").get("nome")),
                        "%" + nomePaciente.toLowerCase() + "%"
                ));
            }

            if (nomeMedico != null && !nomeMedico.isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.join("usuario").get("nome")),
                        "%" + nomeMedico.toLowerCase() + "%"
                ));
            }

            if (statusPericia != null) {
                predicates.add(cb.equal(root.get("statusPericia"), statusPericia));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

