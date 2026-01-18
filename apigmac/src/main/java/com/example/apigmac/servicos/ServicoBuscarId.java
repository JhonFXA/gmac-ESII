package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ServicoBuscarId {

    @Autowired
    private RepositorioUsuario repositorioUser;

    public ExibeUsuarioDTO buscarUsuario(UUID id) {
        Usuario usuario = repositorioUser.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        return new ExibeUsuarioDTO(
                usuario.getLogin(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getNome(),
                usuario.getPerfil(),
                usuario.getDataNascimento()
        );
    }
}
