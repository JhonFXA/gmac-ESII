package com.example.apigmac.servicos.periciaServicos;

import com.example.apigmac.DTOs.PaginaPericiaDTO;
import com.example.apigmac.entidades.Pericia;
import com.example.apigmac.entidades.Usuario;
import com.example.apigmac.modelo.enums.Perfil;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.repositorios.RepositorioPericia;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.PericiaSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoListarPericia {

    // Repositório utilizado para identificar o perfil do usuário autenticado
    @Autowired
    private RepositorioUsuario repositorioUsuario;

    // Repositório responsável pela consulta das perícias conforme os filtros aplicados
    @Autowired
    private RepositorioPericia repositorioPericia;

    /**
     * Lista perícias aplicando filtros dinâmicos e respeitando
     * as restrições de visibilidade conforme o perfil do usuário logado.
     */
    public List<PaginaPericiaDTO> listarPericia(
            String nomePaciente,
            String nomeMedico,
            StatusPericia statusPericia,
            boolean decrescente){

        // Recupera o contexto de autenticação para validação de acesso
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        // Garante que apenas usuários autenticados possam acessar a listagem
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        // Restringe automaticamente o filtro ao médico logado,
        // impedindo visualização de perícias de outros profissionais
        if (usuarioLogado.getPerfil() == Perfil.MEDICO) {
            nomeMedico = usuarioLogado.getNome();
        }

        // Define a ordenação conforme a preferência recebida na requisição
        Sort sort;
        if (decrescente) {
            sort = Sort.by("dataPericia").descending();
        } else {
            sort = Sort.by("dataPericia").ascending();
        }

        // Define um status padrão para evitar listagens excessivas ou inconsistentes
        StatusPericia statusParaFiltrar;
        if (statusPericia == null) {
            statusParaFiltrar = StatusPericia.AGENDADA;
        } else {
            statusParaFiltrar = statusPericia;
        }

        // Construção dinâmica da Specification para permitir filtros combináveis
        Specification<Pericia> spec =
                PericiaSpecs.filtrar(nomePaciente, nomeMedico, statusParaFiltrar);

        // Execução da consulta respeitando filtros e ordenação definidos
        List<Pericia> paginaEntidades =
                repositorioPericia.findAll(spec, sort);

        // Conversão das entidades para DTO, expondo apenas os dados necessários
        return paginaEntidades.stream().map(pericia -> new PaginaPericiaDTO(
                pericia.getId().toString(),
                pericia.getDocumentacao().getId().toString(),
                pericia.getPaciente().getNome(),
                pericia.getUsuario().getNome(),
                pericia.getStatusPericia(),
                pericia.getDataPericia().toString()
        )).toList();
    }
}
