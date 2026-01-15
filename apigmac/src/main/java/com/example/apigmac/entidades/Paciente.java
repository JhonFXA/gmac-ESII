package com.example.apigmac.entidades.paciente;

import com.example.apigmac.entidades.endereco.Endereco;
import com.example.apigmac.modelo.enums.EstadoCivil;
import com.example.apigmac.modelo.enums.Sexo;
import com.example.apigmac.modelo.enums.StatusSolicitacao;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Table(name = "paciente")
@Entity
public class Paciente {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String cpf;

    private String telefone;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao statusSolicitacao;

    private String urlDocumentacao;

    private LocalDate DataNascimento;

    // mappedBy diz: "O controle desta relação está no campo 'paciente' da classe Endereco"
    // orphanRemoval garante que se você remover um endereço da lista, ele seja deletado do banco
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos;

}
