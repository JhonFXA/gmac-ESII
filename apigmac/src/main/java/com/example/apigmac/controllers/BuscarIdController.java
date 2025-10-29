package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.servicos.ServicoBuscarId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("buscar")
public class BuscarIdController {

    @Autowired
    ServicoBuscarId servicoBuscarId;

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuario(@PathVariable UUID id) {
        try {
            RegistroUsuarioDTO dto = servicoBuscarId.buscarUsuario(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
        }
    }
}
