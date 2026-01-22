package com.example.apigmac.utils;

import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PacienteSpecs {
    public static Specification<Paciente> filtrar(
            String nome,
            String cpf,
            StatusSolicitacao statusSolicitacao) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nome != null && !nome.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("nome")),
                                "%" + nome.toLowerCase() + "%"
                        )
                );
            }

            if (cpf != null && !cpf.isBlank()) {
                predicates.add(
                        cb.equal(root.get("cpf"), cpf)
                );
            }

            if (statusSolicitacao != null) {
                predicates.add(
                        cb.equal(root.get("statusSolicitacao"), statusSolicitacao)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
