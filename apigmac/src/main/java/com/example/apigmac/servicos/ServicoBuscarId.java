package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ServicoBuscarId {

    @Autowired
    private RepositorioUsuario repositorioUser;

    public RegistroUsuarioDTO buscarUsuario(UUID id) {
        Usuario usuario = repositorioUser.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        return new RegistroUsuarioDTO(
                usuario.getLogin(),
                usuario.getEmail(),
                null, // senha não deve ser retornada
                usuario.getCpf(),
                usuario.getNome(),
                usuario.getPerfil(),
                usuario.getDataNascimento()
        );
    }
}
