package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.servicos.usuariosServicos.ServicoAlterarUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoBuscarUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoListarUsuario;
import com.example.apigmac.servicos.usuariosServicos.ServicoRegistro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador responsável pelo gerenciamento de usuários do sistema.
 */
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private ServicoAlterarUsuario servicoAlterarUsuario;

    @Autowired
    private ServicoRegistro servicoRegistro;

    @Autowired
    private ServicoBuscarUsuario servicoBuscarUsuario;

    @Autowired
    private ServicoListarUsuario servicoListarUsuario;

    /**
     * Atualiza os dados de um usuário a partir do CPF atual.
     */
    @PutMapping("/alterar/{cpfAtual}")
    public ResponseEntity<?> alterarUsuario(
            @RequestBody AlterarUsuarioDTO dados,
            @PathVariable String cpfAtual) {

        try {
            servicoAlterarUsuario.alterarUsuario(dados, cpfAtual);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao alterar usuário"));
        }
    }

    /**
     * Retorna os dados de um usuário a partir do CPF.
     */
    @GetMapping("/buscar/{cpf}")
    public ResponseEntity<?> buscarUsuario(@PathVariable String cpf) {
        try {
            ExibeUsuarioDTO usuario = servicoBuscarUsuario.buscarUsuario(cpf);
            return ResponseEntity.ok(usuario);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao buscar usuário"));
        }
    }

    /**
     * Realiza o cadastro de um novo usuário no sistema.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados) {
        try {
            servicoRegistro.cadastrarUsuario(dados);
            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao cadastrar usuário"));
        }
    }

    /**
     * Lista usuários aplicando filtros opcionais.
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) Perfil perfil,
            @RequestParam(defaultValue = "true") boolean ordemDecrescente) {

        try {
            List<ExibeUsuarioDTO> usuarios =
                    servicoListarUsuario.listarUsuarios(nome, cpf, perfil, ordemDecrescente);

            return ResponseEntity.ok(usuarios);

        } catch (IllegalArgumentException ex) {
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
