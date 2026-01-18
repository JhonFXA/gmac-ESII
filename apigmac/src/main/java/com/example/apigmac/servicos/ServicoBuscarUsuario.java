package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoBuscarUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    public ExibeUsuarioDTO buscarUsuario(String cpf) {
        Usuario usuario = (Usuario) repositorioUsuario.findByCpf(cpf);

        if (usuario == null) {
            throw new RuntimeException("Usuario não encontrado");
        }

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
