package com.example.apigmac.entidades;

import com.example.apigmac.modelo.enums.StatusDocumentacao;
import com.example.apigmac.modelo.enums.StatusValidacaoDocumentacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "validacao_documentacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacaoDocumentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_validacao", nullable = false)
    private LocalDate dataValidacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_validacao", nullable = false)
    private StatusValidacaoDocumentacao statusValidacaoDocumentacao;

    @Column(name = "observacao", nullable = false)
    private String observacao;

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
