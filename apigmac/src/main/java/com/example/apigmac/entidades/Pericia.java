package com.example.apigmac.entidades;


import com.example.apigmac.modelo.enums.StatusPericia;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "pericia")
@Getter
@Setter
public class Pericia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_pericia", nullable = false)
    private LocalDate dataPericia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pericia", nullable = false)
    private StatusPericia statusPericia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Medico medico;
}
