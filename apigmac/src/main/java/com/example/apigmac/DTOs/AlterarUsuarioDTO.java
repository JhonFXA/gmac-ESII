package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.Perfil;

import java.time.LocalDate;
import java.util.UUID;

public record AlterarUsuarioDTO(String login, String email, String senha, String cpf, String nome, Perfil perfil, String especializacao, LocalDate dataNascimento) {
}
