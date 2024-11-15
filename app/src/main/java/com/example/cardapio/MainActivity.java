package com.example.cardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // EditText
    private EditText usuario;
    private EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        usuario = findViewById(R.id.editTextUsuario);
        senha = findViewById(R.id.editTextSenha);

        Button login = findViewById(R.id.buttonLogin);
        Button criarConta = findViewById(R.id.buttonCriarConta);

        login.setOnClickListener((v) -> {
            String usuarioValue = usuario.getText().toString();
            String senhaValue = senha.getText().toString();

            if (usuarioValue.isEmpty()) usuario.setError("O campo usuário não pode ser vazio");
            if (senhaValue.isEmpty()) senha.setError("O campo senha não pode ser vazio");

            if (!usuarioValue.isEmpty() && !senhaValue.isEmpty()) {
                Toast.makeText(v.getContext(), "Dados válidos", Toast.LENGTH_LONG).show();
            }
        });

        criarConta.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, CriarConta.class);
            startActivity(intent);
            Toast.makeText(v.getContext(), "Entrou na tela de cadastro", Toast.LENGTH_LONG).show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }
}