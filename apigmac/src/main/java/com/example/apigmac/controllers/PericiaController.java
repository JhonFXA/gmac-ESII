package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.PaginaPericiaDTO;
import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.DTOs.RemarcarPericiaDTO;
import com.example.apigmac.DTOs.ValidacaoPericiaDTO;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.servicos.periciaServicos.ServicoCancelarPericia;
import com.example.apigmac.servicos.periciaServicos.ServicoListarPericia;
import com.example.apigmac.servicos.periciaServicos.ServicoMarcarPericia;
import com.example.apigmac.servicos.periciaServicos.ServicoRealizarPericia;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("pericia")
public class PericiaController {

    // Serviço responsável pelo agendamento de perícias
    @Autowired
    private ServicoMarcarPericia servicoMarcarPericia;

    // Serviço responsável pela validação e conclusão da perícia
    @Autowired
    private ServicoRealizarPericia servicoRealizarPericia;

    // Serviço responsável pela listagem e filtragem de perícias
    @Autowired
    private ServicoListarPericia servicoListarPericia;

    // Serviço responsável por cancelamento e remarcação de perícias
    @Autowired
    private ServicoCancelarPericia servicoCancelarPericia;

    /**
     * Endpoint responsável por iniciar o processo de agendamento de uma perícia médica.
     */
    @PostMapping("/marcar")
    public ResponseEntity<Map<String, String>> marcarPericia(@RequestBody @Valid PericiaDTO dados) {

        try {
            servicoMarcarPericia.marcarPericia(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao marcar perícia"));
        }
    }

    /**
     * Endpoint responsável por listar perícias com filtros dinâmicos e ordenação configurável.
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarPericias(
            @RequestParam(required = false) String nomePaciente,
            @RequestParam(required = false) String nomeMedico,
            @RequestParam(required = false) StatusPericia statusPericia,
            @RequestParam(defaultValue = "true") boolean decrescente) {

        try {
            List<PaginaPericiaDTO> paginaPericiaDTOS =
                    servicoListarPericia.listarPericia(nomePaciente, nomeMedico, statusPericia, decrescente);
            return ResponseEntity.ok(paginaPericiaDTOS);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao listar perícias"));
        }
    }

    /**
     * Endpoint responsável por registrar o resultado da perícia
     * e propagar seus efeitos para a documentação relacionada.
     */
    @PutMapping("/validarPericia")
    public ResponseEntity<Map<String, String>> realizarPericia(@RequestBody ValidacaoPericiaDTO dto) {
        try {
            servicoRealizarPericia.validarPericia(
                    dto.validacaoDocumentacaoDTO(),
                    dto.periciaId()
            );

            return ResponseEntity.ok(
                    Map.of("mensagem", "Perícia validada com sucesso")
            );

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
                    .body(Map.of("erro", "Erro interno ao validar perícia"));
        }
    }

    /**
     * Endpoint responsável por cancelar uma perícia e atualizar o estado da solicitação associada.
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, String>> cancelar(@PathVariable UUID id) {
        try {
            servicoCancelarPericia.cancelarPericia(id);
            return ResponseEntity.ok(
                    Map.of("mensagem", "Perícia cancelada e solicitação finalizada.")
            );

        } catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao cancelar perícia"));
        }
    }

    /**
     * Endpoint responsável por reagendar uma perícia já existente,
     * mantendo o histórico e o controle de estado.
     */
    @PutMapping("/{id}/remarcar")
    public ResponseEntity<Map<String, String>> remarcar(
            @PathVariable UUID id,
            @RequestBody RemarcarPericiaDTO remarcarPericiaDTO) {

        try {
            LocalDateTime novaData = remarcarPericiaDTO.data();
            servicoCancelarPericia.remarcarPericia(id, novaData);

            return ResponseEntity.ok(
                    Map.of("mensagem", "Perícia remarcada com sucesso para " + novaData)
            );

        } catch (NoSuchElementException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao remarcar perícia"));
        }
    }

}
