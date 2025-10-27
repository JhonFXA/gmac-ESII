package com.example.apigmac.entidades.endereco;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

}
