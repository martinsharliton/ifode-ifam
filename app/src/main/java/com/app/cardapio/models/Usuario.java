package com.app.cardapio.models;

import com.app.cardapio.enums.TipoPerfil;
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
    private String dataNascimento;
    private boolean ativo;
    private TipoPerfil tipoPerfil;
}
