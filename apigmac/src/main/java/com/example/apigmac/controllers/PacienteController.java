package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.AlterarPacienteDTO;
import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.servicos.ServicoAlterarPaciente;
import com.example.apigmac.servicos.ServicoBuscarPaciente;
import com.example.apigmac.servicos.ServicoCadastrarPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("paciente")
public class PacienteController {

    @Autowired
    private ServicoCadastrarPaciente servicoCadastrarPaciente;


    @Autowired
    private ServicoAlterarPaciente servicoAlterarPaciente;

    @Autowired
    private ServicoBuscarPaciente servicoBuscarPaciente;

    @PostMapping(value = "/cadastrar",consumes = "multipart/form-data")
    public ResponseEntity<?> cadastrarPaciente(@RequestPart("dados") PacienteDTO dados, @RequestPart(value = "documento",required = false) MultipartFile documento)
    {
        try {
            Paciente paciente = servicoCadastrarPaciente.cadastrarPaciente(dados,documento);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao cadastrar paciente"));
        }
    }

    @PutMapping("/alterar")
    public ResponseEntity<?> alterarPaciente(@RequestBody AlterarPacienteDTO dto){
        try {
            servicoAlterarPaciente.alterarPaciente(dto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex) {
            // Dados inválidos enviados pelo cliente
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            // Paciente não encontrado
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            // Erro inesperado
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro interno ao alterar paciente"
                    ));
        }
    }


    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<?> buscarPaciente(@PathVariable String cpf){
        try {
            PacienteDTO dto = servicoBuscarPaciente.buscarPaciente(cpf);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao buscar paciente"));
        }
    }

    @PostMapping(value = "/{cpf}/documento",consumes = "multipart/form-data")
    public ResponseEntity<?> adicionarDocumento(@PathVariable("cpf") String cpf, @RequestPart(value = "documento",required = false) MultipartFile documento)
    {
        try {
            servicoCadastrarPaciente.cadastrarDocumento(documento,cpf);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao adicionar documento"));
        }
    }

    @PostMapping("/{cpf}/endereco")
    public ResponseEntity<?> adicionarEndereco(@RequestBody EnderecoDTO enderecoDTO,@PathVariable String cpf){
        try {
            servicoCadastrarPaciente.cadastrarEndereco(enderecoDTO,cpf);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao adicionar endereço"));
        }

    }
}
