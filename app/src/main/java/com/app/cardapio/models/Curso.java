package com.app.cardapio.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {
    private int id;
    private String nome;
    private String descricao;
    private int duracaoSemestres;
    private int cargaHoraria;
    private String coordenador;
    private boolean ativo;
}
