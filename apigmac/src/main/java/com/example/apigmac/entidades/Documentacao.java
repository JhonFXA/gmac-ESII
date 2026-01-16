package com.example.apigmac.entidades;


import com.example.apigmac.modelo.enums.StatusDocumentacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "documentacao")
@Getter
@Setter
public class Documentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String caminho;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_documentacao", nullable = false)
    private StatusDocumentacao statusDocumentacao;

    @Column(nullable = false)
    private LocalDate dataEnvio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;
}
