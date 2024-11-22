package com.app.cardapio.models;

import com.app.cardapio.enums.TipoPerfil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Administrador extends Usuario {
    private String departamento;
    private String nivelAcesso;

    public Administrador(String matricula, String nome, String email, String senha, String telefone, String endereco, String dataNascimento, boolean ativo, TipoPerfil tipoPerfil, String departamento, String nivelAcesso) {
        super(matricula, nome, email, senha, telefone, endereco, dataNascimento, ativo, tipoPerfil);
        this.departamento = departamento;
        this.nivelAcesso = nivelAcesso;
    }
}
