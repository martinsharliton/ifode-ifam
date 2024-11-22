package com.app.cardapio.models;

import com.app.cardapio.enums.TipoPerfil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Aluno extends Usuario {
    private String curso;
    private int qtdCreditos;
    private int semestreAtual;
    private double notaMedia;
    private String dataMatricula;

    public Aluno(String matricula, String nome, String email, String senha, String telefone, String endereco, String dataNascimento, boolean ativo, TipoPerfil tipoPerfil, String curso, int qtdCreditos, int semestreAtual, double notaMedia, String dataMatricula) {
        super(matricula, nome, email, senha, telefone, endereco, dataNascimento, ativo, tipoPerfil);
        this.curso = curso;
        this.qtdCreditos = qtdCreditos;
        this.semestreAtual = semestreAtual;
        this.notaMedia = notaMedia;
        this.dataMatricula = dataMatricula;
    }
}
