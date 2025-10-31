package com.example.apigmac.DTOs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcecaoDTO {
    private int status;
    private String error;
    private String message;
}
