package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioPaciente extends JpaRepository<Paciente, UUID>, JpaSpecificationExecutor<Paciente> {
    Paciente findByCpf(String cpf);
    Paciente findByEmail(String email);
}
