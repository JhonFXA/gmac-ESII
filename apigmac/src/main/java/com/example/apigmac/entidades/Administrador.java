package com.example.apigmac.entidades;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Table(name = "administrador")
@Entity
@Setter
@Getter
@AllArgsConstructor
public class Administrador {

    @Id
    @Column(name = "id_usuario")
    private UUID idUsuario;

    @OneToOne(cascade = CascadeType.PERSIST)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
