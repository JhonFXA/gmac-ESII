package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DocumentacaoSpecs {

    public static Specification<Documentacao> filtrar(String cpf, String nome, StatusDocumentacao status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Acessando a relação: Documentacao -> Paciente
            // O "root.join("paciente")" faz o INNER JOIN no banco de dados
            if (cpf != null && !cpf.isEmpty()) {
                predicates.add(cb.equal(root.join("paciente").get("cpf"), cpf));
            }

            if (nome != null && !nome.isEmpty()) {
                // Filtra pelo nome do paciente
                predicates.add(cb.like(
                        cb.lower(root.join("paciente").get("nome")), // use o nome exato do campo na classe Paciente
                        "%" + nome.toLowerCase() + "%"
                ));
            }

            // Filtro na própria entidade Documentacao
            if (status != null) {
                predicates.add(cb.equal(root.get("statusDocumentacao"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
