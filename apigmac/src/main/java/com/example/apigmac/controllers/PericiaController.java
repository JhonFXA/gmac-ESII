package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.ListaPericiaDTO;
import com.example.apigmac.DTOs.PaginaPericiaDTO;
import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.DTOs.ValidacaoDocumentacaoDTO;
import com.example.apigmac.DTOs.ValidacaoPericiaDTO;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.servicos.ServicoListarPericia;
import com.example.apigmac.servicos.ServicoMarcarPericia;
import com.example.apigmac.servicos.ServicoRealizarPericia;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
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

    @Autowired
    private ServicoListarPericia servicoListarPericia;

    @PostMapping("/marcar")
    public ResponseEntity<Map<String, String>> marcarPericia(@RequestBody @Valid PericiaDTO dados) {

        try {
            servicoMarcarPericia.marcarPericia(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", ex.getMessage()));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarPericias(@RequestParam(required = false) String nomePaciente,
                                                                 @RequestParam(required = false) String nomeMedico,
                                                                 @RequestParam(required = false) StatusPericia statusPericia,
                                                                 @RequestParam(defaultValue = "true") boolean decrescente,
                                                                 @RequestParam(defaultValue = "0") int pagina,
                                                                 @RequestParam(defaultValue = "10") int tamanhoPagina) {
        try {
        Page<PaginaPericiaDTO> paginaPericiaDTOS = servicoListarPericia.listarPericia(nomePaciente,nomeMedico,statusPericia,decrescente,pagina,tamanhoPagina);
        return ResponseEntity.ok(new PagedModel<>(paginaPericiaDTOS));
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
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
