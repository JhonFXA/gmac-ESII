package com.example.apigmac.infra.seguranca;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracoesWeb implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registro){
        registro.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");

    }
}
