package com.example.apigmac.servicos;

import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicoAutorizacao implements UserDetailsService {
    @Autowired
    RepositorioUsuario repositorio;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repositorio.findByLogin(username);
    }
}
