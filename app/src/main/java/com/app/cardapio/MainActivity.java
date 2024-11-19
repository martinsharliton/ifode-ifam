package com.app.cardapio;

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

    private EditText usuario;
    private EditText senha;

    Button login;
    Button criarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        usuario = findViewById(R.id.editTextUsuario);
        senha = findViewById(R.id.editTextSenha);

        login = findViewById(R.id.buttonLogin);
        criarConta = findViewById(R.id.buttonCriarConta);

        login.setOnClickListener((v) -> {
            String usuarioValue = usuario.getText().toString();
            String senhaValue = senha.getText().toString();

            if (usuarioValue.isEmpty()) usuario.setError("O campo usuário não pode ser vazio");
            if (senhaValue.isEmpty()) senha.setError("O campo senha não pode ser vazio");

            if (!usuarioValue.isEmpty() && !senhaValue.isEmpty()) {
                Toast.makeText(v.getContext(), "Dados válidos", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });

        criarConta.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, CriarConta.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }
}