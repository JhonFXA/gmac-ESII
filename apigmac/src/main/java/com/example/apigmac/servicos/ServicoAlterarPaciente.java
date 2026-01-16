package com.example.apigmac.servicos;

import com.example.apigmac.DTOs.AlterarPacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicoAlterarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    @Autowired
    ServicoVerificacao verificacao;

    public void alterarPaciente(AlterarPacienteDTO dto) {

        Paciente paciente = repositorioPaciente.findById(dto.id())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (dto.cpf() != null) {
            if (!verificacao.cpfValido(dto.cpf())) {
                throw new IllegalArgumentException("CPF inválido");
            }

            Paciente existente = (Paciente) repositorioPaciente.findByCpf(dto.cpf());
            if (existente != null && !existente.getId().equals(paciente.getId())) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }

            paciente.setCpf(dto.cpf());
        }

        if (dto.telefone() != null) {
            if (!verificacao.telefoneValido(dto.telefone())) {
                throw new IllegalArgumentException("Telefone inválido");
            }
            paciente.setTelefone(dto.telefone());
        }

        if (dto.email() != null) {
            if (!verificacao.emailValido(dto.email())) {
                throw new IllegalArgumentException("E-mail inválido ou domínio inexistente");
            }

            Paciente existente = (Paciente) repositorioPaciente.findByEmail(dto.email());
            if (existente != null && !existente.getId().equals(paciente.getId())) {
                throw new IllegalArgumentException("E-mail já cadastrado");
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

        repositorioPaciente.save(paciente);
    }

}
