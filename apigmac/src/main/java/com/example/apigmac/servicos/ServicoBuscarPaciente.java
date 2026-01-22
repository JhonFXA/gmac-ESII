package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.repositorios.RepositorioUsuario;
import com.example.apigmac.utils.CpfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ServicoBuscarPaciente {
    @Autowired
    private RepositorioPaciente repositorioPaciente;

    public PacienteDTO buscarPaciente(String cpf){
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);

        if (paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
        }

        List<EnderecoDTO> enderecosDTO = paciente.getEnderecos()
                .stream()
                .map(endereco -> new EnderecoDTO(
                        endereco.getCep(),
                        endereco.getCidade(),
                        endereco.getEstado(),
                        endereco.getBairro(),
                        endereco.getNumero(),
                        endereco.getLogradouro(),
                        endereco.getComplemento()
                ))
                .toList();



        return new PacienteDTO(
                paciente.getNome(),
                CpfUtils.formatar(paciente.getCpf()),
                paciente.getStatusSolicitacao(),
                paciente.getDataNascimento(),
                paciente.getTelefone(),
                paciente.getEmail(),
                paciente.getSexo(),
                paciente.getEstadoCivil(),
                enderecosDTO
                );
    }
}
