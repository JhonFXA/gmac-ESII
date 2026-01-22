package com.example.apigmac.infra.seguranca;


import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.servicos.documentacaoServicos.ServicoToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
public class FiltroSeguranca extends OncePerRequestFilter {

    @Autowired
    ServicoToken servicoToken;

    @Autowired
    RepositorioUsuario repositorio;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recuperarToken(request);
        if (token != null){
            var login = servicoToken.validarToken(token);
            UserDetails usuario = repositorio.findByLogin(login);
            if (usuario == null) {
                throw new AccessDeniedException("Usuário não tem permissão ou token inválido.");
            }
            var autenticacao = new UsernamePasswordAuthenticationToken(usuario,null,usuario.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }
        filterChain.doFilter(request, response);
    }

    private String recuperarToken (HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ","");
    }
}
