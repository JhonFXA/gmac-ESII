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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoListarPericia {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioPericia repositorioPericia;

    public List<PaginaPericiaDTO> listarPericia(
            String nomePaciente,
            String nomeMedico,
            StatusPericia statusPericia,
            boolean decrescente){
//            int pagina,
//            int tamanho) {

        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        if (usuarioLogado.getPerfil() == Perfil.MEDICO) {
            nomeMedico = usuarioLogado.getNome();
        }

        Sort sort;
        if (decrescente) {
            sort = Sort.by("dataPericia").descending();
        } else {
            sort = Sort.by("dataPericia").ascending();
        }
//
//        if (pagina < 0 || tamanho <= 0) {
//            throw new IllegalArgumentException("Parâmetros de paginação inválidos");
//        }

//        Pageable pageable = PageRequest.of(pagina, tamanho, sort);

        StatusPericia statusParaFiltrar;
        if (statusPericia == null) {
            statusParaFiltrar = StatusPericia.AGENDADA;
        } else {
            statusParaFiltrar = statusPericia;
        }

        Specification<Pericia> spec = PericiaSpecs.filtrar(nomePaciente, nomeMedico, statusParaFiltrar);
//        Page<Pericia> paginaEntidades = repositorioPericia.findAll(spec, pageable);
        List<Pericia> paginaEntidades = repositorioPericia.findAll(spec,sort);

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

