package com.example.apigmac.entidades;

import com.example.apigmac.modelo.enums.Perfil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "usuario")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true,nullable = false)
    private String login;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(unique = true,nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;



    public Usuario(String login, String email, String senha, String cpf, String nome, Perfil perfil, LocalDate dataNascimento){
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
        if(this.perfil == Perfil.ADMINISTRADOR) return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
        new SimpleGrantedAuthority("ROLE_MEDICO");
        new SimpleGrantedAuthority("ROLE_RECEPCIONISTA");
        if(this.perfil == Perfil.MEDICO) return List.of(new SimpleGrantedAuthority("ROLE_MEDICO"));
        else return List.of(new SimpleGrantedAuthority("ROLE_RECEPCIONISTA"));
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
