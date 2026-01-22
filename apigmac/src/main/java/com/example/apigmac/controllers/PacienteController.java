package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.*;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.servicos.ServicoAlterarPaciente;
import com.example.apigmac.servicos.ServicoBuscarPaciente;
import com.example.apigmac.servicos.ServicoCadastrarPaciente;
import com.example.apigmac.servicos.ServicoListarPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("paciente")
public class PacienteController {

    @Autowired
    private ServicoCadastrarPaciente servicoCadastrarPaciente;


    @Autowired
    private ServicoAlterarPaciente servicoAlterarPaciente;

    @Autowired
    private ServicoBuscarPaciente servicoBuscarPaciente;

    @Autowired
    private ServicoListarPaciente servicoListarPaciente;

    @PostMapping(value = "/cadastrar",consumes = "multipart/form-data")
    public ResponseEntity<?> cadastrarPaciente(@RequestPart("dados") PacienteDTO dados, @RequestPart(value = "documento",required = false) MultipartFile documento)
    {
        try {
            Paciente paciente = servicoCadastrarPaciente.cadastrarPaciente(dados,documento);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }

    @PutMapping("/alterar")
    public ResponseEntity<?> alterarPaciente(@RequestBody AlterarPacienteDTO dto){
        try {
            servicoAlterarPaciente.alterarPaciente(dto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }


    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<?> buscarPaciente(@PathVariable String cpf){
        try {
            PacienteDTO dto = servicoBuscarPaciente.buscarPaciente(cpf);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }

    @PostMapping(value = "/{cpf}/documento",consumes = "multipart/form-data")
    public ResponseEntity<?> adicionarDocumento(@PathVariable("cpf") String cpf, @RequestPart(value = "documento",required = false) MultipartFile documento)
    {
        try {
            servicoCadastrarPaciente.cadastrarDocumento(documento,cpf);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex){
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }

    @PostMapping("/{cpf}/endereco")
    public ResponseEntity<?> adicionarEndereco(@RequestBody EnderecoDTO enderecoDTO,@PathVariable String cpf){
        try {
            servicoCadastrarPaciente.cadastrarEndereco(enderecoDTO,cpf);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }
    @GetMapping("/listar")
    public ResponseEntity<?> listarPaciente(@RequestParam(required = false) String nome,
                                            @RequestParam(required = false) String cpf,
                                            @RequestParam(required = false)StatusSolicitacao statusSolicitacao,
                                            @RequestParam(defaultValue = "true") boolean decrescente,
                                            @RequestParam(defaultValue = "0") int pagina,
                                            @RequestParam(defaultValue = "10") int tamanhoPagina) {
        try {
            Page<PaginaPacienteDTO> exibePacientesDTOs = servicoListarPaciente.listarPacientes(nome,cpf,statusSolicitacao,decrescente,pagina,tamanhoPagina);
            return ResponseEntity.ok(new PagedModel<>(exibePacientesDTOs));
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }
}
