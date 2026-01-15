package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.entidades.Endereco;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioEndereco;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicoEndereco {

    @Autowired
    RepositorioEndereco repositorioEndereco;

    public Endereco cadastrarEndereco(EnderecoDTO enderecoDTO, Paciente paciente){
        Endereco endereco = new Endereco(
                enderecoDTO.cep(),
                enderecoDTO.cidade(),
                enderecoDTO.estado(),
                enderecoDTO.bairro(),
                enderecoDTO.logradouro(),
                enderecoDTO.numero(),
                enderecoDTO.complemento()
        );
        endereco.setPaciente(paciente);
        endereco = repositorioEndereco.save(endereco);
        return endereco;
    }
}
