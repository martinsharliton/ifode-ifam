package com.app.cardapio.models;

import com.app.cardapio.enums.TipoPerfil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Administrador{
    private String matricula;
    private String nome;
    private String email;
    private boolean status_matricula;
    private String usuario;
    private String Campus;
}
