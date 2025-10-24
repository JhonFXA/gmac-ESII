package com.example.apigmac.dominio.usuario;

import com.example.apigmac.modelo.enums.Perfil;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Table(name = "usuario")
@Entity
public class Usuario {
    @Id
    @GeneratedValue
    private UUID id;

    private String email;
    private String senha;
    private String cpf;
    private String nome;
    private Perfil perfil;
}
