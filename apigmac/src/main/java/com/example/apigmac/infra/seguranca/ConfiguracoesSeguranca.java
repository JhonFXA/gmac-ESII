package com.example.apigmac.infra.seguranca;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracoesSeguranca {

    @Autowired
    FiltroSeguranca filtroSeguranca;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"/usuario/buscar/{cpf}").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST,"/usuario/registro").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/usuario/alterar").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET,"/usuario/listar").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/paciente/cadastrar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                        .requestMatchers(HttpMethod.POST, "/paciente/{cpf}/endereco").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                        .requestMatchers(HttpMethod.POST, "/paciente/{cpf}/documento").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA")
                        .requestMatchers(HttpMethod.PUT,"/paciente/alterar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.GET,"/paciente/buscar/{cpf}").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.GET,"/paciente/listar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.GET,"/documentacao/buscar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.GET,"/documentacao/url/{id}").hasAnyRole("ADMINISTRADOR", "MEDICO")
                        .requestMatchers(HttpMethod.GET,"/documentacao/validar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.POST, "/pericia/marcar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.PUT, "/pericia/validarPericia").hasRole("MEDICO")
                        .requestMatchers(HttpMethod.POST, "/pericia/listar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.PUT,"/pericia/{id}/cancelar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .requestMatchers(HttpMethod.PUT,"/pericia/{id}/remarcar").hasAnyRole("ADMINISTRADOR", "RECEPCIONISTA", "MEDICO")
                        .anyRequest().authenticated()
        )
        .addFilterBefore(filtroSeguranca, UsernamePasswordAuthenticationFilter.class)
        .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
