package com.example.apigmac.dominio.documentacao;


import com.example.apigmac.dominio.medico.Medico;
import com.example.apigmac.dominio.paciente.Paciente;
import com.example.apigmac.modelo.enums.StatusDocumentacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Table(name = "documentacao")
@Entity
@Getter
@Setter

public class Documentacao {
    @Id
    @GeneratedValue
    private UUID id;

    private Date dataValidacao;
    private StatusDocumentacao statusDocumentacao;

    @OneToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Medico medico;


}
