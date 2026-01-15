package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.servicos.ServicoRegistro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class RegistroController {

    @Autowired
    private ServicoRegistro registroService;

    @PostMapping("/registro")
    public ResponseEntity cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados) {
        try {
            Usuario usuario = registroService.cadastrarUsuario(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }
}
