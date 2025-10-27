package com.example.apigmac.entidades.paciente;

import com.example.apigmac.entidades.endereco.Endereco;
import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Table(name = "paciente")
@Entity
public class Paciente {
    @Id
    @GeneratedValue
    private UUID id;

    private String cpf;
    private String telefone;
    private String email;
    private String sexo;
    private EstadoCivil estadoCivil;
    private StatusSolicitacao statusSolicitacao;
    private String urlDocumentacao;
    private Date DataNascimento;

    @OneToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

}
