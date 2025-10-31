package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.DTOs.TokenDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class ServicoLogin {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ServicoToken servicoToken;

    public TokenDTO login(LoginDTO loginDTO) {
        var authToken = new UsernamePasswordAuthenticationToken(loginDTO.login(), loginDTO.senha());
        var auth = authenticationManager.authenticate(authToken);

        // auth.getPrincipal() é o usuário carregado pelo ServicoAutorizacao
        var token = servicoToken.gerarToken((Usuario) auth.getPrincipal());

        if (((Usuario) auth.getPrincipal()).getPerfil() == Perfil.INATIVO) {
            throw new DisabledException("Usuário inativo");
        }
        return new TokenDTO(token);
    }
}
