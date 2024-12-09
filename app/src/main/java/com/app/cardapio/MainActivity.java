package com.app.cardapio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
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
    private boolean senhaVisivel = false;

    @SuppressLint("ClickableViewAccessibility")
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
                usuario.setError("teste " + AlunoAuth.getInstance().getDocumentId());
                return;
            }
            if (senhaValue.isEmpty()) {
                senha.setError("O campo senha não pode ser vazio");
                return;
            }

            verificarAluno(usuarioValue, senhaValue);
        });

        senha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Verifica se o evento foi na área do drawableEnd
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable drawableEnd = senha.getCompoundDrawablesRelative()[2]; // DrawableEnd
                    if (drawableEnd != null && event.getRawX() >= (senha.getRight() - drawableEnd.getBounds().width())) {
                        toggleSenhaVisibilidade();
                        return true; // Consumiu o evento
                    }
                }
                return false;
            }
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
                            startActivity(new Intent(this, Home.class));
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

    private void toggleSenhaVisibilidade() {
        if (senhaVisivel) {
            // Muda para senha oculta
            senha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            senha.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_senha_off, 0);
        } else {
            // Muda para senha visível
            senha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            senha.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_senha_on, 0);
        }
        senhaVisivel = !senhaVisivel;

        // Move o cursor para o final
        senha.setSelection(senha.getText().length());
    }
}
