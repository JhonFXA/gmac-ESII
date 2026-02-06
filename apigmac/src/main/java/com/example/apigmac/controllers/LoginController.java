package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.DTOs.LoginUsuarioDTO;
import com.example.apigmac.servicos.loginServicos.ServicoLogin;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class LoginController {

    // Serviço responsável pela autenticação do usuário
    @Autowired
    private ServicoLogin servicoLogin;

    /**
     * Realiza a autenticação do usuário a partir das credenciais informadas.
     * Retorna os dados do usuário autenticado ou mensagens de erro apropriadas
     * conforme o tipo de falha ocorrida.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            // Executa o processo de autenticação
            LoginUsuarioDTO usuarioLogado = servicoLogin.login(loginDTO);

            // Retorna os dados do usuário autenticado
            return ResponseEntity.ok(usuarioLogado);

        } catch (BadCredentialsException ex) {
            // Credenciais inválidas (usuário ou senha incorretos)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Login ou senha inválidos"));

        } catch (DisabledException ex) {
            // Usuário desativado ou sem permissão de acesso
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", ex.getMessage()));

        } catch (Exception ex) {
            // Tratamento genérico para erros inesperados
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno ao realizar login"));
        }
    }
}
