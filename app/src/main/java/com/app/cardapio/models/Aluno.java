package com.app.cardapio.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario{
    private int id;
    private String curso;
    private boolean status_matricula;
    private int qtd_creditos;

    public Aluno(String matricula, String nome, String email, String senha, int id, String curso, boolean status_matricula, int qtd_creditos) {
        super(matricula, nome, email, senha);
        this.id = id;
        this.curso = curso;
        this.status_matricula = status_matricula;
        this.qtd_creditos = qtd_creditos;
    }
}
