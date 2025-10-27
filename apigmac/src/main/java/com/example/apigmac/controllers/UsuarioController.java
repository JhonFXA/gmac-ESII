package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class UsuarioController {

    @Autowired
    private RepositorioUsuario repositorio;

    @PostMapping("/regristro")
    public ResponseEntity<Void> cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados){
        Usuario usuario = new Usuario(dados);
        repositorio.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
