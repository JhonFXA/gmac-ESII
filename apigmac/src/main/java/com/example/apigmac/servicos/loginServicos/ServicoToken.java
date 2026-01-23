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

    @Value("${api.security.token.secret}")
    private String chave;

    public String gerarToken(Usuario usuario){
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chave);
            String token = JWT.create()
                    .withIssuer("api")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(tempoExpiracao())
                    .sign(algoritmo);
            return token;
        } catch (JWTCreationException ex){
            throw new IllegalStateException("Erro ao gerar token JWT", ex);
        }
    }

    public String validarToken(String token){
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chave);
            return JWT.require(algoritmo)
                    .withIssuer("api")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            return "";
        }

    }

    public Instant tempoExpiracao(){
        return LocalDateTime.now().plusHours(12).toInstant(ZoneOffset.of("-03:00"));
    }

}
