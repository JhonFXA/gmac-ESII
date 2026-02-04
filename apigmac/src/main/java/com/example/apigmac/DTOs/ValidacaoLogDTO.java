package com.example.apigmac.DTOs;

import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusValidacaoDocumentacao;

import java.time.LocalDate;
import java.util.UUID;

public record ValidacaoLogDTO(String usuario,
                              String paciente,
                              StatusValidacaoDocumentacao statusValidacaoDocumentacao,
                              StatusDocumentacao statusDocumentacao,
                              String observacao,
                              LocalDate data
)
{
}
