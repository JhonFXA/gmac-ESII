package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Cadastro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioLogCadastroPaciente extends JpaRepository<Cadastro, UUID> {
}
