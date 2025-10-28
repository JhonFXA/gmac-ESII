package com.example.apigmac.entidades.usuario;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.modelo.enums.Perfil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "usuario")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;

    private String login;
    private String email;
    private String senha;
    private String cpf;
    private String nome;

    @Enumerated(EnumType.STRING)
    private Perfil perfil;
    private Date dataNascimento;


    public Usuario(String login, String email, String senha, String cpf, String nome, Perfil perfil, Date dataNascimento){
        this.login = login;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.nome = nome;
        this.perfil = perfil;
        this.dataNascimento = dataNascimento;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
