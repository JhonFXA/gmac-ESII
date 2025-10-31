package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.servicos.ServicoAlterarUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("alterar")
public class AlterarUserController {

    @Autowired
    private ServicoAlterarUser servicoAlterarUser;

    @PutMapping
    public ResponseEntity alterarUsuario(@RequestBody AlterarUsuarioDTO dto){
        try {
            servicoAlterarUser.alterarUsuario(dto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }
}
