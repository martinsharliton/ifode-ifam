package com.app.cardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.cardapio.models.AlunoAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText usuario;
    private EditText senha;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        usuario = findViewById(R.id.editTextUsuario);
        senha = findViewById(R.id.editTextSenha);
        login = findViewById(R.id.buttonLogin);

        login.setOnClickListener(v -> {
            String usuarioValue = usuario.getText().toString().trim();
            String senhaValue = senha.getText().toString().trim();

            if (usuarioValue.isEmpty()) {
                usuario.setError("O campo usuário não pode ser vazio");
                return;
            }
            if (senhaValue.isEmpty()) {
                senha.setError("O campo senha não pode ser vazio");
                return;
            }

           verificarAluno(usuarioValue, senhaValue);
            verificarAdministrador(usuarioValue, senhaValue);
        });
    }

    private void verificarAluno(String usuario, String senha) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("aluno")
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String senhaArmazenada = document.getString("senha");
                        if (senhaArmazenada != null && senhaArmazenada.equals(senha)) {
                            String documentId = document.getId();
                            AlunoAuth.getInstance().setDocumentId(documentId);
                            Toast.makeText(this, "Bem-vindo, Aluno!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, Home.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Usuário ou senha incorretos. [ALUNO]", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        verificarAdministrador(usuario, senha);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao acessar o banco de dados.", Toast.LENGTH_LONG).show();
                });
    }

    private void verificarAdministrador(String usuario, String senha) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("administrador")
                .whereEqualTo("usuario", usuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String senhaArmazenada = document.getString("senha");
                        if (senhaArmazenada != null && senhaArmazenada.equals(senha)) {
                            String documentId = document.getId();
                            AlunoAuth.getInstance().setDocumentId(documentId);
                            Toast.makeText(this, "Bem-vindo, Administrador!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, homeAdm.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Usuário ou senha incorretos. [ADMINISTRADOR]", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Usuário ou senha incorretos. [EM NADA]", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao acessar o banco de dados.", Toast.LENGTH_LONG).show();
                });
    }
}
