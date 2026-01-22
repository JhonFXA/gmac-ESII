package com.example.apigmac;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class ApigmacApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApigmacApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Define o fuso horário padrão para Brasília (UTC-3)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));

        // Opcional: imprimir no console para confirmar na inicialização
        System.out.println("Fuso horário configurado para: " + TimeZone.getDefault().getID());
    }
}