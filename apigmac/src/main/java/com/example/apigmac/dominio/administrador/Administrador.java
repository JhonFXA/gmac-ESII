package com.example.apigmac.dominio.administrador;


import com.example.apigmac.dominio.usuario.Usuario;
import jakarta.persistence.*;

import java.util.UUID;

@Table(name = "administrador")
@Entity
public class Administrador {
    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
