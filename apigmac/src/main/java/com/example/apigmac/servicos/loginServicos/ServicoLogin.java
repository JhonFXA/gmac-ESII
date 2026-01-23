package com.example.apigmac.servicos.loginServicos;

import com.example.apigmac.DTOs.LoginDTO;
import com.example.apigmac.DTOs.LoginUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
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

    public LoginUsuarioDTO login(LoginDTO loginDTO) {
        var authToken = new UsernamePasswordAuthenticationToken(loginDTO.login(), loginDTO.senha());
        var auth = authenticationManager.authenticate(authToken);

        var usuarioLogado = (Usuario)auth.getPrincipal();
        if (usuarioLogado.getPerfil() == Perfil.INATIVO) {
            throw new DisabledException("Usuário inativo");
        }
        var token = servicoToken.gerarToken(usuarioLogado);
        return new LoginUsuarioDTO(token,usuarioLogado.getLogin(),usuarioLogado.getNome(),usuarioLogado.getPerfil().toString(),usuarioLogado.getEmail(),usuarioLogado.getCpf(),usuarioLogado.getDataNascimento().toString());


    }
}
