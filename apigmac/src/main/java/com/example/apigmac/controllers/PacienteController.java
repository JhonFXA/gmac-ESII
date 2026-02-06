package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.*;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.servicos.pacientesServicos.ServicoAlterarPaciente;
import com.example.apigmac.servicos.pacientesServicos.ServicoBuscarPaciente;
import com.example.apigmac.servicos.pacientesServicos.ServicoCadastrarPaciente;
import com.example.apigmac.servicos.pacientesServicos.ServicoListarPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controlador responsável por expor os endpoints
 * relacionados ao gerenciamento de pacientes.
 */
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

    /**
     * Realiza o cadastro de um paciente, permitindo o envio opcional de documento.
     */
    @PostMapping(value = "/cadastrar", consumes = "multipart/form-data")
    public ResponseEntity<?> cadastrarPaciente(
            @RequestPart("dados") PacienteDTO dados,
            @RequestPart(value = "documento", required = false) MultipartFile documento) {

        try {
            // Delegação da lógica de cadastro para a camada de serviço
            Paciente paciente = servicoCadastrarPaciente.cadastrarPaciente(dados, documento);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            // Erros de validação de dados enviados pelo cliente
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            // Falhas inesperadas no processo de cadastro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao cadastrar paciente"));
        }
    }

    /**
     * Atualiza os dados de um paciente identificado pelo CPF atual.
     */
    @PutMapping("/alterar/{cpfAtual}")
    public ResponseEntity<?> alterarPaciente(
            @RequestBody AlterarPacienteDTO dto,
            @PathVariable String cpfAtual) {

        try {
            // Executa a alteração mantendo as regras de negócio na camada de serviço
            servicoAlterarPaciente.alterarPaciente(dto, cpfAtual);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (IllegalArgumentException ex) {
            // Dados inválidos enviados pelo cliente
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            // Paciente não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            // Erro inesperado durante a atualização
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao alterar paciente"));
        }
    }

    /**
     * Retorna os dados de um paciente a partir do CPF informado.
     */
    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<?> buscarPaciente(@PathVariable String cpf) {

        try {
            // Recupera os dados do paciente para exibição
            PacienteDTO dto = servicoBuscarPaciente.buscarPaciente(cpf);
            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException ex) {
            // CPF ou parâmetros inválidos
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            // Paciente não encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            // Erro inesperado na busca
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao buscar paciente"));
        }
    }

    /**
     * Adiciona um documento ao paciente identificado pelo CPF.
     */
    @PostMapping(value = "/{cpf}/documento", consumes = "multipart/form-data")
    public ResponseEntity<?> adicionarDocumento(
            @PathVariable("cpf") String cpf,
            @RequestPart(value = "documento", required = false) MultipartFile documento) {

        try {
            // Delegação da associação do documento ao paciente
            servicoCadastrarPaciente.cadastrarDocumento(documento, cpf);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao adicionar documento"));
        }
    }

    /**
     * Adiciona um endereço ao paciente identificado pelo CPF.
     */
    @PostMapping("/{cpf}/endereco")
    public ResponseEntity<?> adicionarEndereco(
            @RequestBody EnderecoDTO enderecoDTO,
            @PathVariable String cpf) {

        try {
            // Associa o endereço ao paciente correspondente
            servicoCadastrarPaciente.cadastrarEndereco(enderecoDTO, cpf);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao adicionar endereço"));
        }
    }

    /**
     * Lista pacientes aplicando filtros opcionais e ordenação.
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarPaciente(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) StatusSolicitacao statusSolicitacao,
            @RequestParam(defaultValue = "true") boolean decrescente) {
//            @RequestParam(defaultValue = "0") int pagina,
//            @RequestParam(defaultValue = "10") int tamanhoPagina) {

        try {
            // Recupera a lista de pacientes conforme critérios informados
            List<PaginaPacienteDTO> exibePacientesDTOs =
                    servicoListarPaciente.listarPacientes(
                            nome, cpf, statusSolicitacao, decrescente);

            return ResponseEntity.ok(exibePacientesDTOs);

        } catch (Exception ex) {
            // Erro genérico durante a listagem
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }
}
