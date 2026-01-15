package com.example.apigmac.entidades.administrador;


import com.example.apigmac.entidades.usuario.Usuario;
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

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
