package com.app.cardapio.models;

import com.app.cardapio.enums.TipoPerfil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Administrador extends Usuario {

    public Administrador(String matricula, String nome, String email, String senha, String dataNascimento, boolean ativo, TipoPerfil tipoPerfil) {
        super(matricula, nome, email, senha, dataNascimento, ativo, tipoPerfil);
    }
}
