package com.example.apigmac.utils;

import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSpecs {

    public static Specification<Usuario> filtrar(
            String nome,
            String cpf,
            Perfil perfil) {

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

            if (perfil != null) {
                predicates.add(
                        cb.equal(root.get("perfil"), perfil)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
