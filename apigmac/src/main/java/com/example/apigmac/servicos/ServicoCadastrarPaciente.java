package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Endereco;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioEndereco;
import com.example.apigmac.repositorios.RepositorioPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicoCadastrarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    @Autowired
    private RepositorioEndereco repositorioEndereco;

    @Autowired
    private ServicoVerificacao verificacao;

    public Paciente cadastrarPaciente(PacienteDTO dados){
//        if(repositorioPaciente.findByCpf(dados.cpf()) != null){
//            throw new IllegalArgumentException("Usuario já cadastrado!");
//        }

        Paciente paciente = new Paciente(dados.cpf(), dados.telefone(), dados.email(), dados.sexo(), dados.estadoCivil(), dados.statusSolicitacao(), dados.dataNascimento());

        paciente = repositorioPaciente.saveAndFlush(paciente);

        for (EnderecoDTO dto : dados.enderecos()) {
            Endereco endereco = new Endereco(
                    dto.cep(),
                    dto.cidade(),
                    dto.estado(),
                    dto.bairro(),
                    dto.logradouro(),
                    dto.numero(),
                    dto.complemento()
            );

            endereco.setPaciente(paciente);
            repositorioEndereco.saveAndFlush(endereco);
        }

        return paciente;
    }

}
