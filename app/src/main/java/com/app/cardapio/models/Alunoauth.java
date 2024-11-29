package com.app.cardapio.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AlunoAuth {
    private static AlunoAuth instance;
    private String documentId;

    private AlunoAuth() { }

    public static AlunoAuth getInstance() {
        if (instance == null) {
            instance = new AlunoAuth();
        }
        return instance;
    }

}
