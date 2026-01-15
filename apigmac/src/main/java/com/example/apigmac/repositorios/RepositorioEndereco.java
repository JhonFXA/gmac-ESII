package com.example.apigmac.repositorios;

import com.example.apigmac.entidades.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositorioEndereco extends JpaRepository<Endereco, UUID> {
}
