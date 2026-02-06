package com.example.apigmac.servicos.loginServicos;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.apigmac.entidades.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class ServicoToken {

    // Chave secreta utilizada para assinatura e validação do token JWT
    @Value("${api.security.token.secret}")
    private String chave;

    /**
     * Gera um token JWT para o usuário autenticado.
     *
     * @param usuario usuário autenticado no sistema
     * @return token JWT assinado
     * @throws IllegalStateException caso ocorra erro na geração do token
     */
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chave);

            return JWT.create()
                    .withIssuer("api")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(tempoExpiracao())
                    .sign(algoritmo);

        } catch (JWTCreationException ex) {
            // Encapsula exceção da biblioteca para não expor detalhes internos
            throw new IllegalStateException("Erro ao gerar token JWT", ex);
        }
    }

    /**
     * Valida o token JWT e retorna o login do usuário.
     *
     * @param token token JWT recebido na requisição
     * @return login do usuário se o token for válido, ou string vazia caso inválido
     */
    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chave);

            return JWT.require(algoritmo)
                    .withIssuer("api")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException ex) {
            // Token inválido, expirado ou adulterado
            return "";
        }
    }

    /**
     * Define o tempo de expiração do token JWT.
     *
     * @return instante de expiração do token
     */
    private Instant tempoExpiracao() {
        return LocalDateTime.now()
                .plusHours(12)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
