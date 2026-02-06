package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.DocumentoDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.DTOs.ValidacaoLogDTO;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.servicos.documentacaoServicos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("documentacao")
public class DocumentacaoController {

    // Serviço responsável pela listagem de documentações
    @Autowired
    private ServicoListarDocumentacao servicoListarDocumentacao;

    // Serviço responsável por operações de transformação e acesso a documentos
    @Autowired
    private ServicoTransformarDocumentacao servicoTransformarDocumentacao;

    // Serviço responsável pela validação de documentações
    @Autowired
    private ServicoValidarDocumentacao servicoValidarDocumentacao;

    // Serviço responsável pela busca individual de documentações
    @Autowired
    private ServicoBuscarDocumentacao servicoBuscarDocumentacao;

    // Serviço responsável pela consulta de logs de validação
    @Autowired
    private ServicoBuscarValidacao servicoBuscarValidacao;

    /**
     * Lista documentações com filtros opcionais.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarDocumentacao(
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) StatusDocumentacao status,
            @RequestParam(defaultValue = "false") boolean decrescente
    ) {
        try {
            List<DocumentoDTO> resultado =
                    servicoListarDocumentacao.listarDocumentos(cpf, nome, status, decrescente);

            return ResponseEntity.ok(resultado);

        } catch (Exception ex) {
            // Erro de entrada ou regra de negócio
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", ex.getMessage()));
        }
    }

    /**
     * Gera uma URL temporária para acesso ao documento armazenado.
     */
    @GetMapping("/url/{id}")
    public ResponseEntity<?> gerarUrlDocumentacao(@PathVariable UUID id) {
        try {
            String urlDocumentacao =
                    servicoTransformarDocumentacao.gerarPresignedUrl(id);

            return ResponseEntity.ok(urlDocumentacao);

        } catch (NoSuchElementException ex) {
            // Documento não encontrado
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (IllegalArgumentException ex) {
            // ID inválido
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (S3Exception ex) {
            // Falha de comunicação com o S3
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro ao acessar o S3",
                            "detalhe", ex.awsErrorDetails().errorMessage()
                    ));

        } catch (Exception ex) {
            // Erro inesperado
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro interno do servidor",
                            "detalhe", ex.getMessage()
                    ));
        }
    }

    /**
     * Registra a validação de uma documentação.
     */
    @PostMapping("/validar")
    public ResponseEntity<?> validarDocumentacao(
            @RequestBody ValidacaoDocumentacaoDTO validacaoDocumentacaoDTO) {
        try {
            servicoValidarDocumentacao.registrarValidacao(validacaoDocumentacaoDTO);

            return ResponseEntity.ok(
                    Map.of("mensagem", "Documentação validada com sucesso")
            );

        } catch (IllegalArgumentException ex) {
            // Dados inválidos
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (NoSuchElementException ex) {
            // Documentação não encontrada
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (AccessDeniedException ex) {
            // Usuário sem permissão para validar
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            // Erro inesperado
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "erro", "Erro interno ao validar documentação"
                    ));
        }
    }

    /**
     * Busca uma documentação específica pelo identificador.
     */
    @GetMapping("buscar/{id}")
    public ResponseEntity<?> buscarDocumentacao(@PathVariable String id) {
        try {
            DocumentoDTO dto = servicoBuscarDocumentacao.buscarPorId(id);
            return ResponseEntity.ok(dto);

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
                    .body(Map.of("erro", "Erro interno ao buscar documentação"));
        }
    }

    /**
     * Busca o log de validação de uma documentação.
     */
    @GetMapping("buscar/validacao/{id}")
    public ResponseEntity<?> buscarValidacao(@PathVariable String id) {
        try {
            ValidacaoLogDTO dto =
                    servicoBuscarValidacao.buscarValidacaoPorId(id);

            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException ex) {
            // Validação não encontrada
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }
}
