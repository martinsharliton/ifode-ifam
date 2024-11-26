package com.app.cardapio.models;

public class Alunoauth {
    private static Alunoauth instance;
    private String documentId;

    private Alunoauth() { }

    public static Alunoauth getInstance() {
        if (instance == null) {
            instance = new Alunoauth();
        }
        return instance;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
