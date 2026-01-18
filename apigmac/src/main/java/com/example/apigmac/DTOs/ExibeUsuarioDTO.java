package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.Perfil;

import java.time.LocalDate;
import java.util.Date;

public record ExibeUsuarioDTO(String login, String email, String cpf, String nome, Perfil perfil, LocalDate dataNascimento){

}
