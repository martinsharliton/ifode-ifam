package com.app.cardapio.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private String matricula;
    private String nome;
    private String email;
    private String senha;
}