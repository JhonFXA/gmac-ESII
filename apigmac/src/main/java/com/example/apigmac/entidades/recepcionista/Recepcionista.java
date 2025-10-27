package com.example.apigmac.entidades.recepcionista;

import com.example.apigmac.entidades.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
