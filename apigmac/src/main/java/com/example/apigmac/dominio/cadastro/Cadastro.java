package com.example.apigmac.dominio.cadastro;

import com.example.apigmac.dominio.medico.Medico;
import com.example.apigmac.dominio.paciente.Paciente;
import com.example.apigmac.dominio.recepcionista.Recepcionista;
import jakarta.persistence.*;

import java.util.Date;

@Table(name = "cadastro")
@Entity
public class Cadastro {
    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_paciente")
    private Paciente paciente;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Recepcionista recepcionista;

    private Date dataCadastro;

}
