package com.example.apigmac.dominio.paciente;

import com.example.apigmac.dominio.endereco.Endereco;
import com.example.apigmac.dominio.usuario.Usuario;
import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.*;

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

    @OneToOne
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

}
