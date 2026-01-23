package com.example.apigmac.servicos.usuariosServicos;

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

import java.util.List;

@Service
public class ServicoListarUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    public List<ExibeUsuarioDTO> listarUsuarios(
            String nome,
            String cpf,
            Perfil perfil,
            boolean decrescente){
//            int pagina,
//            int tamanho) {

//        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
//        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

//        if (pagina < 0) {
//            throw new IllegalArgumentException("Página não pode ser negativa");
//        }
//
//        if (tamanho <= 0) {
//            throw new IllegalArgumentException("Tamanho da página deve ser maior que zero");
//        }



        // 📌 Ordenação SOMENTE por nome
        Sort sort = decrescente
                ? Sort.by("nome").descending()
                : Sort.by("nome").ascending();
//
//        Pageable pageable = PageRequest.of(pagina, tamanho, sort);

        Specification<Usuario> spec =
                UsuarioSpecs.filtrar(nome, cpf, perfil);

        List<Usuario> usuarios =
                repositorioUsuario.findAll(spec,sort);
//
//        List<Usuario> paginaEntidades =
//                repositorioUsuario.findAll(spec, pageable);

//        return paginaEntidades.map(usuario ->
//                new ExibeUsuarioDTO(
//                        usuario.getLogin(),
//                        usuario.getEmail(),
//                        usuario.getCpf(),
//                        usuario.getNome(),
//                        usuario.getPerfil(),
//                        usuario.getDataNascimento()
//                )
//        );
//    }
        return usuarios.stream()
                .map(usuario -> new ExibeUsuarioDTO(
                        usuario.getLogin(),
                        usuario.getEmail(),
                        usuario.getCpf(),
                        usuario.getNome(),
                        usuario.getPerfil(),
                        usuario.getDataNascimento()
                ))
                .toList();
    }
}
