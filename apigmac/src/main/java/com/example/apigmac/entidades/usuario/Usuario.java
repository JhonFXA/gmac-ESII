package com.example.apigmac.entidades.usuario;

import com.example.apigmac.DTOs.RegistroUsuarioDTO;
import com.example.apigmac.modelo.enums.Perfil;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "usuario")
@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue
    private UUID id;

    private String login;
    private String email;
    private String senha;
    private String cpf;
    private String nome;
    private Perfil perfil;

    public Usuario(RegistroUsuarioDTO dados){
        this.login = dados.login();
        this.email = dados.email();
        this.senha = dados.senha();
        this.cpf = dados.cpf();
        this.nome = dados.nome();
        this.perfil = dados.perfil();
       // this.dataNascimento = dados.dataNascimento();
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
