package com.example.apigmac.servicos.usuariosServicos;

import com.example.apigmac.DTOs.ExibeUsuarioDTO;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.CpfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pela recuperação de dados de um usuário
 * a partir do CPF informado.
 */
@Service
public class ServicoBuscarUsuario {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    /**
     * Busca um usuário pelo CPF e retorna seus dados para exibição.
     */
    public ExibeUsuarioDTO buscarUsuario(String cpf) {

        // Normaliza o CPF para garantir padronização na consulta
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        // Recupera o usuário correspondente ao CPF informado
        Usuario usuario = (Usuario) repositorioUsuario.findByCpf(cpfNormalizado);

        // Garante que apenas usuários existentes sejam retornados
        if (usuario == null) {
            throw new RuntimeException("Usuario não encontrado");
        }

        // Constrói o DTO com os dados necessários para apresentação
        return new ExibeUsuarioDTO(
                usuario.getLogin(),
                usuario.getEmail(),
                CpfUtils.formatar(usuario.getCpf()),
                usuario.getNome(),
                usuario.getPerfil(),
                usuario.getDataNascimento()
        );
    }
}
