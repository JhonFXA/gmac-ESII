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

    // Responsável por realizar o processo de autenticação do Spring Security
    @Autowired
    private AuthenticationManager authenticationManager;

    // Serviço responsável pela geração do token JWT
    @Autowired
    private ServicoToken servicoToken;

    /**
     * Realiza a autenticação do usuário e gera o token de acesso.
     *
     * @param loginDTO dados de login informados pelo usuário
     * @return objeto com informações do usuário autenticado e token JWT
     * @throws DisabledException caso o usuário esteja inativo
     */
    public LoginUsuarioDTO login(LoginDTO loginDTO) {

        // Cria o token de autenticação a partir do login e senha
        var authToken = new UsernamePasswordAuthenticationToken(
                loginDTO.login(),
                loginDTO.senha()
        );

        // Executa o processo de autenticação
        var auth = authenticationManager.authenticate(authToken);

        // Obtém o usuário autenticado
        var usuarioLogado = (Usuario) auth.getPrincipal();

        // Impede login de usuários inativos
        if (usuarioLogado.getPerfil() == Perfil.INATIVO) {
            throw new DisabledException("Usuário inativo");
        }

        // Gera o token JWT para o usuário autenticado
        var token = servicoToken.gerarToken(usuarioLogado);

        // Retorna os dados necessários para o frontend
        return new LoginUsuarioDTO(
                token,
                usuarioLogado.getLogin(),
                usuarioLogado.getNome(),
                usuarioLogado.getPerfil().toString(),
                usuarioLogado.getEmail(),
                usuarioLogado.getCpf(),
                usuarioLogado.getDataNascimento().toString()
        );
    }
}
