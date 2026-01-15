package com.example.apigmac.entidades;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "cadastro")
public class Cadastro {

    @Id
    @Column(name = "id_paciente")
    private UUID idPaciente;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Recepcionista recepcionista;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
}

