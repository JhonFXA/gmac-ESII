package com.example.apigmac.entidades.endereco;


import com.example.apigmac.entidades.paciente.Paciente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "endereco")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue
    private UUID id;

    private String logradouro;
    private String numero;
    private String complemento;
    private String cidade;
    private String estado;
    private String cep;

    @ManyToOne // Muitos endereços para um paciente
    @JoinColumn(name = "id_paciente") // Nome da coluna que será criada na tabela 'endereco'
    private Paciente paciente;

    public Endereco(String cep,String cidade,String estado,String logradouro,String numero,String complemento){
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
    }

}
