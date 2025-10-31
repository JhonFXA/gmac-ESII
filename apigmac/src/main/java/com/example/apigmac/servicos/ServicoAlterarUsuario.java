package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarUsuarioDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ServicoAlterarUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void alterarUsuario(AlterarUsuarioDTO dto){
        Usuario usuario = repositorioUsuario.findById(dto.id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.nome() != null) usuario.setNome(dto.nome());
        if (dto.cpf() != null) usuario.setCpf(dto.cpf());
        if (dto.email() != null) usuario.setEmail(dto.email());
        if (dto.login() != null) usuario.setLogin(dto.login());
        if (dto.perfil() != null) usuario.setPerfil(dto.perfil());
        if (dto.dataNascimento() != null) usuario.setDataNascimento(dto.dataNascimento());

        if (dto.senha() != null) {
            String senhaCriptografada = passwordEncoder.encode(dto.senha());
            usuario.setSenha(senhaCriptografada);
        }

        repositorioUsuario.save(usuario);

    }
}
