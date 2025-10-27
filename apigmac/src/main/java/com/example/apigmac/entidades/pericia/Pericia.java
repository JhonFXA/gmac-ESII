package com.example.apigmac.entidades.pericia;


import com.example.apigmac.entidades.documentacao.Documentacao;
import com.example.apigmac.entidades.medico.Medico;
import com.example.apigmac.entidades.paciente.Paciente;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Table(name = "pericia")
@Entity
@Getter
@Setter
public class Pericia {
    @Id
    @GeneratedValue
    private UUID id;

    private Date dataPericia;
    private StatusSolicitacao statusPericia;

    @OneToOne
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @OneToOne
    @JoinColumn(name = "id_documentacao")
    private Documentacao documentacao;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Medico medico;

}
