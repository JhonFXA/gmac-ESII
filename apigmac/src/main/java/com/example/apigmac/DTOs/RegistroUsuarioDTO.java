package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.Perfil;
import java.util.Date;

public record RegistroUsuarioDTO(String login, String email, String senha, String cpf, String nome, Perfil perfil){

}
