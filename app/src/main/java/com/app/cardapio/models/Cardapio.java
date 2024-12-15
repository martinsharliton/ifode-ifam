package com.app.cardapio.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cardapio {
    private String id;
    private String titulo;
    private String descricao;
    private String horario;
    private int imagemId;
}
