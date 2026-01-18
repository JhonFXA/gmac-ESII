package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.DTOs.LoginUsuarioDTO;
import com.example.apigmac.servicos.ServicoLogin;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class LoginController {

    @Autowired
    ServicoLogin servicoLogin;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            LoginUsuarioDTO usuarioLogado = servicoLogin.login(loginDTO);
            return ResponseEntity.ok(usuarioLogado);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("Erro", "Login ou senha inválidos"));
        }catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("Erro", ex.getMessage()));
        }
    }
}
