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

    // Repositório responsável pela persistência e consulta de pacientes
    @Autowired
    private RepositorioPaciente repositorioPaciente;

    // Serviço centralizado para validações de regras de negócio
    @Autowired
    private ServicoVerificacao verificacao;

    /**
     * Altera os dados de um paciente existente.
     * Apenas os campos informados no DTO são atualizados,
     * preservando os demais dados já cadastrados.
     */
    @Transactional
    public void alterarPaciente(AlterarPacienteDTO dto, String cpfAtual) {

        // Validação de entrada para evitar processamento inválido
        if (dto == null) {
            throw new IllegalArgumentException("Digite o campo que deseja alterar");
        }

        if (cpfAtual == null) {
            throw new IllegalArgumentException("CPF é obrigatório para alteração");
        }

        // Normaliza o CPF para garantir consistência na busca
        String cpfNormalizado = CpfUtils.normalizar(cpfAtual);

        // Busca o paciente no banco de dados
        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);

        // Garante que o paciente exista antes de realizar alterações
        if (paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
        }

        // Atualiza o CPF caso tenha sido informado
        if (dto.cpf() != null) {
            if (!verificacao.cpfValido(dto.cpf())) {
                throw new IllegalArgumentException("CPF inválido");
            }
            paciente.setCpf(CpfUtils.normalizar(dto.cpf()));
        }

        // Atualiza o nome, garantindo tamanho mínimo
        if (dto.nome() != null) {
            if (!verificacao.textoObrigatorioValido(dto.nome(), 3)) {
                throw new IllegalArgumentException("Nome inválido");
            }
            paciente.setNome(dto.nome());
        }

        // Atualiza o telefone após validação
        if (dto.telefone() != null) {
            if (!verificacao.telefoneValido(dto.telefone())) {
                throw new IllegalArgumentException("Telefone inválido");
            }
            paciente.setTelefone(dto.telefone());
        }

        // Atualiza o email somente se for diferente do atual
        // e garante unicidade no sistema
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

        // Atualiza o sexo, se informado
        if (dto.sexo() != null) {
            paciente.setSexo(dto.sexo());
        }

        // Atualiza o estado civil, se informado
        if (dto.estadoCivil() != null) {
            paciente.setEstadoCivil(dto.estadoCivil());
        }

        // Atualiza o status da solicitação, se informado
        if (dto.statusSolicitacao() != null) {
            paciente.setStatusSolicitacao(dto.statusSolicitacao());
        }

        // Atualiza a data de nascimento após validação
        if (dto.dataNascimento() != null) {
            if (!verificacao.dataNascimentoValida(dto.dataNascimento())) {
                throw new IllegalArgumentException("Data de nascimento inválida");
            }
            paciente.setDataNascimento(dto.dataNascimento());
        }
    }
}
