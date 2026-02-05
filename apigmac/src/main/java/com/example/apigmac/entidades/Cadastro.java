package com.example.apigmac.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "cadastro")
@NoArgsConstructor
@Setter
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
    private Usuario usuario;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;
}

