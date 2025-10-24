package com.example.apigmac.dominio.recepcionista;

import com.example.apigmac.dominio.usuario.Usuario;
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
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

}
