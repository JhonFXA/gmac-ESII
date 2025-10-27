package com.example.apigmac.entidades.administrador;


import com.example.apigmac.entidades.usuario.Usuario;
import jakarta.persistence.*;

@Table(name = "administrador")
@Entity
public class Administrador {
    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
