package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.servicos.ServicoAlterarUsuario;
import com.example.apigmac.servicos.ServicoBuscarUsuario;
import com.example.apigmac.servicos.ServicoListarUsuario;
import com.example.apigmac.servicos.ServicoRegistro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private ServicoAlterarUsuario servicoAlterarUsuario;

    @Autowired
    private ServicoRegistro registroService;

    @Autowired
    private ServicoBuscarUsuario servicoBuscarUsuario;

    @Autowired
    private ServicoListarUsuario servicoListarUsuario;

    @PutMapping("/alterar")
    public ResponseEntity<?> alterarUsuario(@RequestBody AlterarUsuarioDTO dto){
        try {
            servicoAlterarUsuario.alterarUsuario(dto);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao alterar usuário"));
        }
    }

    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<?> buscarUsuario(@PathVariable String cpf) {
        try {
            ExibeUsuarioDTO dto = servicoBuscarUsuario.buscarUsuario(cpf);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao buscar usuário"));
        }
    }


    @PostMapping("/registro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados) {
        try {
            registroService.cadastrarUsuario(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao cadastrar usuário"));
        }
    }
    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios(@RequestParam(required = false) String nome,
                                            @RequestParam(required = false) String cpf,
                                            @RequestParam(required = false) Perfil perfil,
                                            @RequestParam(defaultValue = "true") boolean decrescente,
                                            @RequestParam(defaultValue = "0") int pagina,
                                            @RequestParam(defaultValue = "10") int tamanhoPagina) {
        try {
            Page<ExibeUsuarioDTO> exibeUsuarioDTOS = servicoListarUsuario.listarUsuarios(nome,cpf,perfil,decrescente,pagina,tamanhoPagina);
            return ResponseEntity.ok(new PagedModel<>(exibeUsuarioDTOS));
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno no servidor"));
        }
    }
}
