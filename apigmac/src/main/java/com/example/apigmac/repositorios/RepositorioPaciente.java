package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface RepositorioPaciente extends JpaRepository<Paciente, UUID> {
    Paciente findByCpf(String cpf);
    Paciente findByEmail(String email);
}
