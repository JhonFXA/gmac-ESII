package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.Perfil;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record ExibeUsuarioDTO(String login, String email, String cpf, String nome, Perfil perfil, LocalDate dataNascimento){

}
