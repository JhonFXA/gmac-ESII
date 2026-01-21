package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.DTOs.ValidacaoPericiaDTO;
import com.example.apigmac.servicos.ServicoMarcarPericia;
import com.example.apigmac.servicos.ServicoRealizarPericia;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("pericia")
public class PericiaController {

    @Autowired
    private ServicoMarcarPericia servicoMarcarPericia;

    @Autowired
    private ServicoRealizarPericia servicoRealizarPericia;

    @PostMapping("/marcar")
    public ResponseEntity<Map<String, String>> marcarPericia(@RequestBody @Valid PericiaDTO dados) {

        try {
            servicoMarcarPericia.marcarPericia(dados);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", ex.getMessage()));
        }

    }

    @PutMapping("/validarPericia")
    public ResponseEntity<Map<String, String>> realizarPericia(@RequestBody ValidacaoPericiaDTO dto) {
        try {
            servicoRealizarPericia.validarPericia(dto.validacaoDocumentacaoDTO(), dto.periciaId());

            return ResponseEntity.ok(
                    Map.of("mensagem", "Perícia validada com sucesso")
            );

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(
                    Map.of("erro", ex.getMessage())
            );
        }
    }

}
