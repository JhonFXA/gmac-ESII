package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarPacienteDTO;
import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoAlterarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    @Autowired
    ServicoVerificacao verificacao;

    @Autowired
    ServicoCadastrarPaciente servicoCadastrarPaciente;

    @Transactional
    public void alterarPaciente(AlterarPacienteDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Digite o Campo que quer mudar");
        }

        Paciente paciente = repositorioPaciente.findByCpf(dto.cpf());
        if (paciente == null) {
            throw new RuntimeException("Paciente não encontrado");
        }

        if (dto.nome() != null) {
            if (!verificacao.textoObrigatorioValido(dto.nome(), 3)) {
                throw new IllegalArgumentException("Nome inválido");
            }
            paciente.setNome(dto.nome());
        }

        if (dto.telefone() != null) {
            if (!verificacao.telefoneValido(dto.telefone())) {
                throw new IllegalArgumentException("Telefone inválido");
            }
            paciente.setTelefone(dto.telefone());
        }

        if (dto.email() != null && !dto.email().equals(paciente.getEmail())) {

            if (!verificacao.emailValido(dto.email())) {
                throw new IllegalArgumentException("Email inválido");
            }

            Paciente existente = repositorioPaciente.findByEmail(dto.email());
            if (existente != null && !existente.getId().equals(paciente.getId())) {
                throw new IllegalArgumentException("Email já cadastrado");
            }

            paciente.setEmail(dto.email());
        }

        if (dto.sexo() != null) {
            paciente.setSexo(dto.sexo());
        }

        if (dto.estadoCivil() != null) {
            paciente.setEstadoCivil(dto.estadoCivil());
        }

        if (dto.statusSolicitacao() != null) {
            paciente.setStatusSolicitacao(dto.statusSolicitacao());
        }

        if (dto.dataNascimento() != null) {
            if (!verificacao.dataNascimentoValida(dto.dataNascimento())) {
                throw new IllegalArgumentException("Data de nascimento inválida");
            }
            paciente.setDataNascimento(dto.dataNascimento());
        }
    }

}
