package com.example.apigmac.infra;

import com.example.apigmac.DTOs.ExcecaoDTO;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class ManipulaExcecoes {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExcecaoDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getRootCause();

        // Verifica se a causa é um erro de parsing de data (DateTimeParseException)
        if (rootCause instanceof JsonMappingException) {
            Throwable causa = rootCause.getCause();
            if (causa instanceof DateTimeParseException) {
                String errorMessage = String.format("Data inválida");

                ExcecaoDTO erro = new ExcecaoDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request", errorMessage);
                return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
            }
        }

        String genericMessage = "Data Inválida.";
        ExcecaoDTO errorResponse = new ExcecaoDTO(HttpStatus.BAD_REQUEST.value(), "Bad Request", genericMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}