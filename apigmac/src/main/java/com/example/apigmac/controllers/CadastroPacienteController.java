package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.servicos.ServicoCadastrarPaciente;
import com.example.apigmac.servicos.ServicoRegistro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class CadastroPacienteController {

    @Autowired
    private ServicoCadastrarPaciente servicoCadastrarPaciente;

    @PostMapping("/cadastrarPaciente")
    public ResponseEntity cadastrarPaciente(@RequestBody PacienteDTO dados) {
        try {
            Paciente paciente = servicoCadastrarPaciente.cadastrarPaciente(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }
}
