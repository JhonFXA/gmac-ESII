package com.example.apigmac.DTOs;

public record LoginUsuarioDTO(String token, String login, String nome, String perfil, String email, String cpf, String dataNascimento) {
}
