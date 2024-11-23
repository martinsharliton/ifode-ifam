package com.app.cardapio;

import com.app.cardapio.enums.TipoPerfil;
import com.app.cardapio.models.Administrador;
import com.app.cardapio.models.Aluno;
import com.app.cardapio.models.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class CriarConta extends AppCompatActivity {

    private EditText matricula;
    private EditText nome;
    private EditText email;
    private EditText senha;
    private EditText confirmarSenha;

    Button cadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_criar_conta);

        matricula = findViewById(R.id.editTextMatricula);
        nome = findViewById(R.id.editTextNomeCompleto);
        email = findViewById(R.id.editTextEmail);
        senha = findViewById(R.id.editTextSenha);
        confirmarSenha = findViewById(R.id.editTextConfirmarSenha);

        cadastrar = findViewById(R.id.buttonCadastrar);


        cadastrar.setOnClickListener((v) -> {
            String matriculaValue = matricula.getText().toString();
            String nomeCompletoValue = nome.getText().toString();
            String emailValue = email.getText().toString();
            String senhaValue = senha.getText().toString();
            String confirmarSenhaValue = confirmarSenha.getText().toString();

            TipoPerfil perfilSelecionado = matriculaValue.contains("@ifam.edu.br") ? TipoPerfil.ALUNO : TipoPerfil.ADMINISTRADOR;

            if (matriculaValue.isEmpty())
                matricula.setError("O campo matrícula não pode ser vazio");
            if (nomeCompletoValue.isEmpty())
                nome.setError("O campo nome não pode ser vazio");
            if (emailValue.isEmpty())
                email.setError("O campo e-mail não pode ser vazio");
            if (senhaValue.isEmpty())
                senha.setError("O campo senha não pode ser vazio");
            if (confirmarSenhaValue.isEmpty())
                confirmarSenha.setError("O campo confirmar senha não pode ser vazio");

            if (!matriculaValue.isEmpty() && !nomeCompletoValue.isEmpty() && !emailValue.isEmpty() && !senhaValue.isEmpty() && !confirmarSenhaValue.isEmpty()) {
                if (senhaValue.equals(confirmarSenhaValue)) {
                    Usuario usuario;

                    if (perfilSelecionado == TipoPerfil.ALUNO) {
                        usuario = new Aluno(
                                matriculaValue,
                                nomeCompletoValue,
                                emailValue,
                                senhaValue,
                                "01/01/2000",
                                true,
                                TipoPerfil.ALUNO,
                                "Curso de teste",
                                20

                        );
                    } else {
                        usuario = new Administrador(
                                matriculaValue,
                                nomeCompletoValue,
                                emailValue,
                                senhaValue,
                                "01/01/2000",
                                true,
                                TipoPerfil.ADMINISTRADOR
                        );
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(perfilSelecionado.name().toLowerCase())
                            .add(usuario)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(v.getContext(), "Usuário salvo com sucesso!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CriarConta.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(v.getContext(), "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                } else {
                    senha.setError("As senhas precisam ser idênticas");
                    confirmarSenha.setError("As senhas precisam ser idênticas");
                }
            }
        });



    }
}