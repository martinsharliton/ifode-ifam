package com.app.cardapio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.app.cardapio.models.AlunoAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Login extends AppCompatActivity {

    private EditText usuario;
    private EditText senha;
    boolean senhaVisivel = false;
    String usuarioValue;
    String senhaValue;

    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            // Se estiver em modo escuro, forçar modo claro
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_login);

        // Inicializa as views
        usuario = findViewById(R.id.editTextUsuario);
        senha = findViewById(R.id.editTextSenha);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        // Verifica se já existe um usuário logado
        verificarLoginSalvo();

        // Recupera os valores passados via Intent
        Intent intent = getIntent();
        if (intent != null) {
            String loginIntent = intent.getStringExtra("nomeUsuario");
            String passwordIntent = intent.getStringExtra("senhaUsuario");

            if (loginIntent != null) {
                usuario.setText(loginIntent);
            }
            if (passwordIntent != null) {
                senha.setText(passwordIntent);
            }
        }

        // Configura botão de login
        buttonLogin.setOnClickListener(v -> {
            usuarioValue = usuario.getText().toString().trim();
            senhaValue = senha.getText().toString().trim();

            if (usuarioValue.isEmpty()) {
                usuario.setError("O campo usuário não pode ser vazio");
                return;
            }
            if (senhaValue.isEmpty()) {
                senha.setError("O campo senha não pode ser vazio");
                return;
            }

            verificarAluno(usuarioValue, senhaValue);
        });

        senha.setOnTouchListener((v, event) -> {
            // Verifica se o evento foi na área do drawableEnd
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = senha.getCompoundDrawablesRelative()[2]; // DrawableEnd
                if (drawableEnd != null && event.getRawX() >= (senha.getRight() - drawableEnd.getBounds().width())) {
                    toggleSenhaVisibilidade();
                    return true; // Consumiu o evento
                }
            }
            return false;
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
                        Boolean statusMatricula = document.getBoolean("status_matricula");
                        String nome = document.getString("nome");

                        if (statusMatricula != null && !statusMatricula) {
                            Toast.makeText(this, "Usuário está inativo.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (senhaArmazenada != null && senhaArmazenada.equals(senha)) {
                            String documentId = document.getId();
                            AlunoAuth.getInstance().setDocumentId(documentId);
                            salvarLogin(documentId, usuario, senha);
                            String primeirosNomes = getDoisPrimeirosNomes(nome);
                            Toast.makeText(this, "Bem-vindo, " + primeirosNomes + "!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, Home.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Usuário ou senha incorretos. [ALUNO]", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        verificarAdministrador(usuario, senha);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao acessar o banco de dados.", Toast.LENGTH_LONG).show());
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
                        Boolean statusMatricula = document.getBoolean("status_matricula");
                        String nome = document.getString("nome");

                        if (statusMatricula != null && !statusMatricula) {
                            Toast.makeText(this, "Usuário está inativo.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (senhaArmazenada != null && senhaArmazenada.equals(senha)) {
                            String documentId = document.getId();
                            AlunoAuth.getInstance().setDocumentId(documentId);
                            salvarLogin(documentId, usuario, senha);
                            String primeirosNomes = getDoisPrimeirosNomes(nome);
                            Toast.makeText(this, "Bem-vindo, " + primeirosNomes + "!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, HomeAdm.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Usuário ou senha incorretos. [ADMINISTRADOR]", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Usuário ou senha incorretos. [EM NADA]", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao acessar o banco de dados.", Toast.LENGTH_LONG).show());
    }

    private String getDoisPrimeirosNomes(String nomeCompleto) {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            return "Usuário";
        }
        String[] partesNome = nomeCompleto.trim().split("\\s+");
        if (partesNome.length >= 2) {
            return partesNome[0] + " " + partesNome[1];
        } else {
            return partesNome[0];
        }
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

    private void salvarLogin(String documentId, String nomeUsuario, String senhaUsuario) {
        getSharedPreferences("appPreferences", MODE_PRIVATE)
                .edit()
                .putString("userId", documentId)      // Salva o ID do usuário
                .putString("nomeUsuario", nomeUsuario) // Salva o nome do usuário
                .putString("senhaUsuario", senhaUsuario) // Salva o e-mail do usuário
                .apply();
    }


    private void verificarLoginSalvo() {
        String userId = getSharedPreferences("appPreferences", MODE_PRIVATE)
                .getString("userId", null);
        if (userId != null) {
            AlunoAuth.getInstance().setDocumentId(userId);
            if (userId.contains("YpcuDQBwAeigcM20dm5J")) {
                startActivity(new Intent(this, HomeAdm.class));
            } else {
                startActivity(new Intent(this, Home.class));
            }
            finish();
        } else {
            String nomeUsuario = getSharedPreferences("appPreferences", MODE_PRIVATE)
                    .getString("nomeUsuario", null);
            String senhaUsuario = getSharedPreferences("appPreferences", MODE_PRIVATE)
                    .getString("senhaUsuario", null);
            usuario.setText(nomeUsuario);
            senha.setText(senhaUsuario);
        }


    }
}
