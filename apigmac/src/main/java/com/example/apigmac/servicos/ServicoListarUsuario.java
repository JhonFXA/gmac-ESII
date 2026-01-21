package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.UsuarioSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ServicoListarUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    public Page<ExibeUsuarioDTO> listarUsuarios(
            String nome,
            String cpf,
            Perfil perfil,
            boolean decrescente,
            int pagina,
            int tamanho) {

//        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
//        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();


        // 📌 Ordenação SOMENTE por nome
        Sort sort = decrescente
                ? Sort.by("nome").descending()
                : Sort.by("nome").ascending();

        Pageable pageable = PageRequest.of(pagina, tamanho, sort);

        Specification<Usuario> spec =
                UsuarioSpecs.filtrar(nome, cpf, perfil);

        Page<Usuario> paginaEntidades =
                repositorioUsuario.findAll(spec, pageable);

        return paginaEntidades.map(usuario ->
                new ExibeUsuarioDTO(
                        usuario.getLogin(),
                        usuario.getEmail(),
                        usuario.getCpf(),
                        usuario.getNome(),
                        usuario.getPerfil(),
                        usuario.getDataNascimento()
                )
        );
    }
}
