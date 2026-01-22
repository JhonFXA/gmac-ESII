package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.DocumentoDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.servicos.documentacaoServicos.ServicoListarDocumentacao;
import com.example.apigmac.servicos.documentacaoServicos.ServicoTransformarDocumentacao;
import com.example.apigmac.servicos.documentacaoServicos.ServicoValidarDocumentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("documentacao")
public class DocumentacaoController {

    @Autowired
    private ServicoListarDocumentacao servicoListarDocumentacao;
    @Autowired
    private ServicoTransformarDocumentacao servicoTransformarDocumentacao;
    @Autowired
    private ServicoValidarDocumentacao servicoValidarDocumentacao;

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarDocumentacao(
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) StatusDocumentacao status,
            @RequestParam(defaultValue = "false") boolean decrescente,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanhoPagina
    ) {
        try {
            Page<DocumentoDTO> resultado = servicoListarDocumentacao.listarDocumentos(cpf, nome, status, decrescente, pagina, tamanhoPagina);
            return ResponseEntity.ok(new PagedModel<>(resultado));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", ex.getMessage()));
        }
    }
    @GetMapping("/url/{id}")
    public ResponseEntity<?> gerarUrlDocumentacao(@PathVariable UUID id) {
        try {

            String urlDocumentacao = servicoTransformarDocumentacao.gerarPresignedUrl(id);
            return ResponseEntity.ok(urlDocumentacao);

        }catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (S3Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro ao acessar o S3",
                            "detalhe", ex.awsErrorDetails().errorMessage()
                    ));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro interno do servidor",
                            "detalhe", ex.getMessage()
                    ));
        }
    }
    @PostMapping("/validar")
    public ResponseEntity<?> validarDocumentacao(@RequestBody ValidacaoDocumentacaoDTO validacaoDocumentacaoDTO){
        try {
            servicoValidarDocumentacao.registrarValidacao(validacaoDocumentacaoDTO);
        return ResponseEntity.ok(
                Map.of("mensagem", "Documentação validada com sucesso")
        );
        }catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro interno ao validar documentação"
                    ));
        }
    }
}
