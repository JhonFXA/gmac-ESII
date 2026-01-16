package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface RepositorioPaciente extends JpaRepository<Paciente, UUID> {
    UserDetails findByCpf(String cpf);
}
