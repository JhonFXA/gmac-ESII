package com.example.apigmac.servicos.usuariosServicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.CpfUtils;
import com.example.apigmac.utils.UsuarioSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pela listagem de usuários do sistema,
 * aplicando filtros dinâmicos e ordenação.
 */
@Service
public class ServicoListarUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    /**
     * Lista usuários de acordo com filtros opcionais e ordem definida.
     */
    public List<ExibeUsuarioDTO> listarUsuarios(
            String nome,
            String cpf,
            Perfil perfil,
            boolean decrescente) {
//            int pagina,
//            int tamanho) {

        // Código comentado mantido para possível uso futuro
        // relacionado à autenticação e paginação

//        var authentication = org.springframework.security.core.context.SecurityContextHolder
//                .getContext().getAuthentication();
//        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        // Validações de paginação mantidas como referência de regra
//        if (pagina < 0) {
//            throw new IllegalArgumentException("Página não pode ser negativa");
//        }
//
//        if (tamanho <= 0) {
//            throw new IllegalArgumentException("Tamanho da página deve ser maior que zero");
//        }

        // Define a ordenação da listagem com base no nome do usuário
        Sort sort = decrescente
                ? Sort.by("nome").descending()
                : Sort.by("nome").ascending();

//        Pageable pageable = PageRequest.of(pagina, tamanho, sort);

        // Cria a especificação dinâmica para filtragem dos usuários
        Specification<Usuario> spec =
                UsuarioSpecs.filtrar(nome, cpf, perfil);

        // Recupera os usuários aplicando filtros e ordenação
        List<Usuario> usuarios =
                repositorioUsuario.findAll(spec, sort);

//        List<Usuario> paginaEntidades =
//                repositorioUsuario.findAll(spec, pageable);

        // Converte as entidades em DTOs para exibição
        return usuarios.stream()
                .map(usuario -> new ExibeUsuarioDTO(
                        usuario.getLogin(),
                        usuario.getEmail(),
                        CpfUtils.formatar(usuario.getCpf()),
                        usuario.getNome(),
                        usuario.getPerfil(),
                        usuario.getDataNascimento()
                ))
                .toList();
    }
}
