package com.example.apigmac.servicos.loginServicos;

import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicoAutorizacao implements UserDetailsService {

    // Repositório responsável pela busca de usuários para autenticação
    @Autowired
    private RepositorioUsuario repositorio;

    /**
     * Carrega os dados do usuário a partir do login informado.
     * Método exigido pelo Spring Security durante o processo de autenticação.
     *
     * @param login login informado pelo usuário
     * @return UserDetails com as informações necessárias para autenticação
     * @throws UsernameNotFoundException caso o usuário não seja encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        // Busca o usuário pelo login
        UserDetails usuario = repositorio.findByLogin(login);

        // Garante que o usuário exista antes de prosseguir com a autenticação
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        // Retorna os dados necessários para o Spring Security
        return usuario;
    }
}
