package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Documentacao;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.repositorios.RepositorioDocumentacao;
import com.example.apigmac.repositorios.RepositorioPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;


@Service
public class ServicoCadastrarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    @Autowired
    private ServicoEndereco servicoEndereco;

    @Autowired
    private RepositorioDocumentacao repositorioDocumentacao;

    @Autowired
    private ServicoVerificacao verificacao;

    @Autowired
    private ServicoTransformarDocumentacao transformarDocumentacao;


    public Paciente cadastrarPaciente(PacienteDTO dados, MultipartFile documento){
//        if(repositorioPaciente.findByCpf(dados.cpf()) != null){
//            throw new IllegalArgumentException("Usuario já cadastrado!");
//        }

        Paciente paciente = new Paciente(dados.cpf(), dados.telefone(), dados.email(), dados.sexo(), dados.estadoCivil(), dados.statusSolicitacao(), dados.dataNascimento());

        String caminho = transformarDocumentacao
                .caminhoDocumentacao(documento, dados.cpf());

        Documentacao documentacao = new Documentacao();
        documentacao.setCaminho(caminho);
        documentacao.setStatusDocumentacao(StatusDocumentacao.PENDENTE);
        documentacao.setDataEnvio(LocalDate.now());
        paciente = repositorioPaciente.save(paciente);
        documentacao.setPaciente(paciente);
        repositorioDocumentacao.save(documentacao);

        for (EnderecoDTO dto : dados.enderecos()) {
            servicoEndereco.cadastrarEndereco(dto,paciente);
        }

        return paciente;
    }

}
