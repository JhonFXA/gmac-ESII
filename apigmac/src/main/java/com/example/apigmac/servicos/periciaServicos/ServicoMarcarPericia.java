package com.example.apigmac.servicos.periciaServicos;

import com.example.apigmac.DTOs.PericiaDTO;
import com.example.apigmac.entidades.*;
import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ServicoMarcarPericia {

    // Repositório responsável pelo controle de existência e persistência das perícias
    @Autowired
    private RepositorioPericia repositorioPericia;

    /**
     * Realiza o agendamento de uma perícia garantindo unicidade por documentação
     * e consistência das regras de data e vínculo entre as entidades.
     */
    public void marcarPericia(PericiaDTO dto){

        // Validação defensiva para evitar processamento com dados incompletos
        if(dto == null){
            throw new IllegalArgumentException("Dados da pericia não informados");
        }

        // Garantia de integridade mínima do fluxo de agendamento
        if (dto.paciente()== null ||
                dto.documentacao()== null ||
                dto.usuario() == null ||
                dto.dataPericia() == null) {

            throw new IllegalArgumentException("Algum campo obrigatório está null no DTO");
        }

        // Impede múltiplas perícias associadas à mesma documentação
        if(repositorioPericia.existsByDocumentacaoId(dto.documentacao().getId())){
            throw new IllegalStateException("Já existe uma perícia marcada para esta documentação");
        }

        // Criação da entidade perícia com validação explícita da regra temporal
        Pericia pericia = new Pericia();
        if(dto.dataPericia().isAfter(LocalDateTime.now())){
            pericia.setDataPericia(dto.dataPericia());
        }else{
            throw new IllegalArgumentException("DATA INVALIDA");
        }

        // Inicialização do estado e associações da perícia
        pericia.setStatusPericia(StatusPericia.AGENDADA);
        pericia.setPaciente(dto.paciente());
        pericia.setUsuario(dto.usuario());
        pericia.setDocumentacao(dto.documentacao());

        // Persistência final após validação completa das regras
        repositorioPericia.save(pericia);
    }
}
