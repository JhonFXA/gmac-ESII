package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {

    @PostMapping ("/login")
    public ResponseEntity<Usuario> login (@RequestBody @Valid LoginDTO loginDTO){
        return ResponseEntity.ok().build();
    }
}
