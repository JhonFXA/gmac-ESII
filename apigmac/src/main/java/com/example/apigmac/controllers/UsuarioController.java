package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.servicos.ServicoAlterarUsuario;
import com.example.apigmac.servicos.ServicoBuscarId;
import com.example.apigmac.servicos.ServicoRegistro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private ServicoAlterarUsuario servicoAlterarUsuario;

    @Autowired
    private ServicoRegistro registroService;

    @Autowired
    private ServicoBuscarId servicoBuscarId;

    @PutMapping("/alterar")
    public ResponseEntity<?> alterarUsuario(@RequestBody AlterarUsuarioDTO dto){
        try {
            servicoAlterarUsuario.alterarUsuario(dto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarUsuario(@PathVariable UUID id) {
        try {
            RegistroUsuarioDTO dto = servicoBuscarId.buscarUsuario(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(Map.of("erro", ex.getMessage()));
        }
    }


    @PostMapping("/registro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados) {
        try {
            registroService.cadastrarUsuario(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }
}
