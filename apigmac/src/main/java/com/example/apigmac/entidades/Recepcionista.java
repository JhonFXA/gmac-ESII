package com.example.apigmac.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "recepcionista")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recepcionista {
    @Id
    @Column(name = "id_usuario")
    private UUID idUsuario;

    @OneToOne(cascade = CascadeType.PERSIST)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}
