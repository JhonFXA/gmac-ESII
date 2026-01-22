package com.example.apigmac.servicos.pacientesServicos;

import com.example.apigmac.DTOs.AlterarPacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.utils.ServicoVerificacao;
import com.example.apigmac.utils.CpfUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ServicoAlterarPaciente {

    @Autowired
    private RepositorioPaciente repositorioPaciente;

    @Autowired
    private ServicoVerificacao verificacao;

    @Transactional
    public void alterarPaciente(AlterarPacienteDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Digite o Campo que quer mudar");
        }

        if (dto.cpf() == null) {
            throw new IllegalArgumentException("CPF é obrigatório para alteração");
        }

        String cpfNormalizado = CpfUtils.normalizar((dto.cpf()));


        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);
        if(paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
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
