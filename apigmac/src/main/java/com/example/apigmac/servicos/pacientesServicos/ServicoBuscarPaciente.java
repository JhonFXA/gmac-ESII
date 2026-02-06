package com.example.apigmac.servicos.pacientesServicos;

import com.example.apigmac.DTOs.EnderecoDTO;
import com.example.apigmac.DTOs.PacienteDTO;
import com.example.apigmac.entidades.Paciente;
import com.example.apigmac.repositorios.RepositorioPaciente;
import com.example.apigmac.utils.CpfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ServicoBuscarPaciente {

    // Repositório responsável pelo acesso aos dados do paciente
    @Autowired
    private RepositorioPaciente repositorioPaciente;

    /**
     * Busca um paciente pelo CPF e retorna seus dados em formato DTO.
     * Aplica validações básicas e garante que apenas dados formatados
     * e consistentes sejam expostos para a camada superior.
     */
    public PacienteDTO buscarPaciente(String cpf) {

        // Validação de entrada para evitar processamento desnecessário
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }

        // Normaliza o CPF para manter padrão interno de consulta
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        // Busca o paciente no banco de dados
        Paciente paciente = repositorioPaciente.findByCpf(cpfNormalizado);

        // Garante que o paciente exista antes de prosseguir
        if (paciente == null) {
            throw new NoSuchElementException("Paciente não encontrado");
        }

        // Converte a lista de entidades Endereco para EnderecoDTO
        // evitando expor entidades diretamente
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

        // Retorna o DTO com os dados do paciente já tratados e formatados
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
