package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.DTOs.TokenDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.servicos.ServicoToken;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    ServicoToken servicoToken;

    @PostMapping ("/login")
    public ResponseEntity login (@RequestBody @Valid LoginDTO loginDTO){
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDTO.login(),loginDTO.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = servicoToken.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(token));
    }
}
