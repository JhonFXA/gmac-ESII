package com.example.apigmac.entidades.recepcionista;

import com.example.apigmac.entidades.usuario.Usuario;
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

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}
