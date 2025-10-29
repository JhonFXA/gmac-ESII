package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("auth")
public class RegistroController {

    @Autowired
    private RepositorioUsuario repositorio;

    @PostMapping("/registro")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados){
        if(this.repositorio.findByLogin(dados.login()) != null){
            return ResponseEntity.badRequest().build();
        }
        String senhaCriptografada = new BCryptPasswordEncoder().encode(dados.senha());
        Usuario usuario = new Usuario(dados.login(), dados.email(), senhaCriptografada, dados.cpf(), dados.nome(), dados.perfil(), dados.dataNascimento());
        repositorio.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<RegistroUsuarioDTO> buscarUsuario(@PathVariable UUID id) {
        return repositorio.findById(id)
                .map(usuario -> {
                    RegistroUsuarioDTO dto = new RegistroUsuarioDTO(
                            usuario.getLogin(),
                            usuario.getEmail(),
                            null,
                            usuario.getCpf(),
                            usuario.getNome(),
                            usuario.getPerfil(),
                            usuario.getDataNascimento()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
