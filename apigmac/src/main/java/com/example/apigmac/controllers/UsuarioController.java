package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class UsuarioController{

    @Autowired
    private RepositorioUsuario repositorio;

    @PostMapping("/registro")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados){
        if(this.repositorio.findByLogin(dados.login()) != null){
            return ResponseEntity.badRequest().build();
        }
        String senhaCriptografada = new BCryptPasswordEncoder().encode(dados.senha());
        Usuario usuario = new Usuario(dados.login(), dados.email(), senhaCriptografada, dados.cpf(), dados.nome(), dados.perfil(), dados.dataNascimento());
        repositorio.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
