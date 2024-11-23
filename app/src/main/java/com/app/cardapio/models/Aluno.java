package com.app.cardapio.models;

import com.app.cardapio.enums.TipoPerfil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario {
    private String curso;
    private int qtdCreditos;

    public Aluno(String matricula, String nome, String email, String senha, String dataNascimento, boolean ativo, TipoPerfil tipoPerfil, String curso, int qtdCreditos) {
        super(matricula, nome, email, senha, dataNascimento, ativo, tipoPerfil);
        this.curso = curso;
        this.qtdCreditos = qtdCreditos;
    }
}
