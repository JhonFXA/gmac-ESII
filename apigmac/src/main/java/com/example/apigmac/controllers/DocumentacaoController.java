package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.DocumentoDTO;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.servicos.ServicoListarDocumentacao;
import com.example.apigmac.servicos.ServicoTransformarDocumentacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("documentacao")
public class DocumentacaoController {

    @Autowired
    private ServicoListarDocumentacao servicoListarDocumentacao;
    @Autowired
    private ServicoTransformarDocumentacao servicoTransformarDocumentacao;

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }
    @GetMapping("/url/{id} ")
    public ResponseEntity<?> gerarUrlDocumentacao(@PathVariable UUID id) {
        try {
            String urlDocumentacao = servicoTransformarDocumentacao.gerarPresignedUrl(id);
            return ResponseEntity.ok(urlDocumentacao);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }
}
