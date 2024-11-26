package com.app.cardapio.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Aluno{
    private String matricula;
    private String nome;
    private String email;
    private String curso;
    private int qtd_creditos;
    private boolean status_matricula;
    private String usuario;
    private String Campus;
}
