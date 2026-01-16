package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.Sexo;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public record PacienteDTO(String cpf, StatusSolicitacao statusSolicitacao, LocalDate dataNascimento, String telefone, String email,
                          Sexo sexo, EstadoCivil estadoCivil, List<EnderecoDTO> enderecos) {
}
