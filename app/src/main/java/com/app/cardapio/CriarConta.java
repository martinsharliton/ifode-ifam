package com.app.cardapio;

import com.app.cardapio.models.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;

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

            if (matriculaValue.isEmpty())
                matricula.setError("O campo matricula não pode ser vazio");
            if (nomeCompletoValue.isEmpty()) nome.setError("O campo nome não pode ser vazio");
            if (emailValue.isEmpty()) email.setError("O campo e-mail não pode ser vazio");
            if (senhaValue.isEmpty()) senha.setError("O campo senha não pode ser vazio");
            if (confirmarSenhaValue.isEmpty())
                confirmarSenha.setError("O campo confirmar senha não pode ser vazio");

            if (!matriculaValue.isEmpty() && !nomeCompletoValue.isEmpty() && !emailValue.isEmpty() && !senhaValue.isEmpty() && !confirmarSenhaValue.isEmpty()) {
                if (senhaValue.equals(confirmarSenhaValue)) {
                    Usuario usuario = new Usuario(matriculaValue, nomeCompletoValue, emailValue, senhaValue);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("usuario")
                            .add(usuario)
                            .addOnSuccessListener(documentReference -> Toast.makeText(v.getContext(), "Usuário salvo com sucesso!", Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show());

                } else {
                    senha.setError("As senhas precisam ser idênticas");
                    confirmarSenha.setError("As senhas precisam ser idênticas");
                }
            }
        });
    }
}