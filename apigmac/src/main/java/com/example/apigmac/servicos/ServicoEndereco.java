package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.entidades.Endereco;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioEndereco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoEndereco {

    @Autowired
    RepositorioEndereco repositorioEndereco;

    public void cadastrarEndereco(EnderecoDTO enderecoDTO, Paciente paciente){
        Endereco endereco = new Endereco(
                enderecoDTO.cep(),
                enderecoDTO.cidade(),
                enderecoDTO.estado(),
                enderecoDTO.bairro(),
                enderecoDTO.logradouro(),
                enderecoDTO.numero(),
                enderecoDTO.complemento()
        );
        paciente.getEnderecos().add(endereco);
        endereco.setPaciente(paciente);
        repositorioEndereco.save(endereco);
    }
}
