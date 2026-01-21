package com.example.apigmac.entidades;


import com.example.apigmac.modelo.enums.StatusPericia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "pericia")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pericia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_pericia", nullable = false)
    private Date dataPericia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pericia", nullable = false)
    private StatusPericia statusPericia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_documentacao", nullable = false)
    private Documentacao documentacao;
}
