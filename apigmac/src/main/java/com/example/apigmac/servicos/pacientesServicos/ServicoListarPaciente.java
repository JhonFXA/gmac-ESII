package com.example.apigmac.servicos.pacientesServicos;

import com.example.apigmac.DTOs.PaginaPacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.utils.CpfUtils;
import com.example.apigmac.utils.PacienteSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoListarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    public List<PaginaPacienteDTO> listarPacientes(
            String nome,
            String cpf,
            StatusSolicitacao statusSolicitacao,
            boolean decrescente){
//            int pagina,
//            int tamanho) {

//        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
//        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();


        Sort sort = decrescente
                ? Sort.by("nome").descending()
                : Sort.by("nome").ascending();

//        Pageable pageable = PageRequest.of(pagina, tamanho, sort);

        Specification<Paciente> spec =
                PacienteSpecs.filtrar(nome, cpf, statusSolicitacao);

//        Page<Paciente> paginaEntidades =
//                repositorioPaciente.findAll(spec, pageable);
       List<Paciente> paginaEntidades =
                repositorioPaciente.findAll(spec,sort);



        return paginaEntidades.stream().map(paciente ->
                new PaginaPacienteDTO(
                        paciente.getNome(),
                        CpfUtils.formatar(paciente.getCpf()),
                        paciente.getStatusSolicitacao(),
                        paciente.getDataNascimento()
                )
        ).toList();
    }
}
