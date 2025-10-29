package com.example.apigmac.controllers;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.entidades.administrador.Administrador;
import com.example.apigmac.entidades.medico.Medico;
import com.example.apigmac.entidades.recepcionista.Recepcionista;
import com.example.apigmac.entidades.usuario.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioAdm;
import com.example.apigmac.repositorios.RepositorioMed;
import com.example.apigmac.repositorios.RepositorioRecepicionista;
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
    private RepositorioUsuario repositorioUser;

    @Autowired
    private RepositorioAdm repositorioAdm;

    @Autowired
    private RepositorioMed repositorioMed;

    @Autowired
    private RepositorioRecepicionista repositorioRecepicionista;

    @PostMapping("/registro")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody RegistroUsuarioDTO dados) {
        if (this.repositorioUser.findByLogin(dados.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String senhaCriptografada = new BCryptPasswordEncoder().encode(dados.senha());
        Usuario usuario = new Usuario(dados.login(), dados.email(), senhaCriptografada, dados.cpf(), dados.nome(), dados.perfil(), dados.dataNascimento());


        if (dados.perfil() == Perfil.ADMINISTRADOR) {
            Administrador admin = new Administrador(usuario.getId(),usuario);
            repositorioAdm.save(admin);
        }else if(dados.perfil() == Perfil.MEDICO){
            Medico med = new Medico(usuario.getId(),usuario, "MEDICO");
            repositorioMed.save(med);
        }else if(dados.perfil() == Perfil.RECEPCIONISTA){
            Recepcionista recep = new Recepcionista(usuario.getId(), usuario);
            repositorioRecepicionista.save(recep);
        }
        usuario = repositorioUser.saveAndFlush(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/buscar/{id}")
    public ResponseEntity<RegistroUsuarioDTO> buscarUsuario(@PathVariable UUID id) {
        return repositorioUser.findById(id)
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
