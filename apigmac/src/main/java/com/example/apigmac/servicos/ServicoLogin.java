package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.DTOs.TokenDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
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

        var usuarioLogado = (Usuario)auth.getPrincipal();
        var token = servicoToken.gerarToken(usuarioLogado);
        return new TokenDTO(token,usuarioLogado.getPerfil().toString());
    }
}
