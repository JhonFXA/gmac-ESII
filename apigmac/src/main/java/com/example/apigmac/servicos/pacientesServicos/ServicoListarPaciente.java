package com.example.apigmac.servicos.pacientesServicos;

import com.example.apigmac.DTOs.PaginaPacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.utils.CpfUtils;
import com.example.apigmac.utils.PacienteSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoListarPaciente {

    // Repositório responsável pela consulta de pacientes
    @Autowired
    private RepositorioPaciente repositorioPaciente;

    /**
     * Lista pacientes aplicando filtros opcionais e ordenação por nome.
     * Retorna apenas os dados necessários para exibição em lista/paginação,
     * garantindo desempenho e desacoplamento da entidade.
     */
    public List<PaginaPacienteDTO> listarPacientes(
            String nome,
            String cpf,
            StatusSolicitacao statusSolicitacao,
            boolean decrescente) {

        // Define a ordenação baseada no parâmetro recebido
        // Centraliza a regra de ordenação para manter clareza
        Sort sort = decrescente
                ? Sort.by("nome").descending()
                : Sort.by("nome").ascending();

        // Cria a especificação dinâmica com base nos filtros informados
        Specification<Paciente> spec =
                PacienteSpecs.filtrar(nome, cpf, statusSolicitacao);

        // Executa a consulta aplicando filtros e ordenação
        List<Paciente> pacientes =
                repositorioPaciente.findAll(spec, sort);

        // Converte a lista de entidades para DTO de listagem
        // evitando exposição desnecessária de dados
        return pacientes.stream()
                .map(paciente -> new PaginaPacienteDTO(
                        paciente.getNome(),
                        CpfUtils.formatar(paciente.getCpf()),
                        paciente.getStatusSolicitacao(),
                        paciente.getDataNascimento()
                ))
                .toList();
    }
}
