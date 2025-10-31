package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.servicos.ServicoAlterarUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("alterar")
public class AlterarUsuarioController {

    @Autowired
    private ServicoAlterarUsuario servicoAlterarUsuario;

    @PutMapping
    public ResponseEntity alterarUsuario(@RequestBody AlterarUsuarioDTO dto){
        try {
            servicoAlterarUsuario.alterarUsuario(dto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }
}
