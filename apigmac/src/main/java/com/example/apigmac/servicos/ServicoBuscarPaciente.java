package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoBuscarPaciente {
    @Autowired
    private RepositorioPaciente repositorioPaciente;

    public PacienteDTO buscarPaciente(String cpf){
        System.out.println(cpf);
        Paciente paciente = repositorioPaciente.findByCpf(cpf);

        if (paciente == null) {
            throw new RuntimeException("Paciente não encontrado");
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
                .toList(); // Java 16+



        return new PacienteDTO(
                paciente.getNome(),
                paciente.getCpf(),
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
