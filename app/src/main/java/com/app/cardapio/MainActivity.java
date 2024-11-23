package com.app.cardapio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;


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
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("aluno")
                        .whereEqualTo("usuario", usuarioValue)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot documents = task.getResult();
                                if (documents.isEmpty()) {
                                    db.collection("administrador")
                                            .whereEqualTo("usuario", usuarioValue)
                                            .get()
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    QuerySnapshot documents2 = task2.getResult();
                                                    if (documents2.isEmpty()) {
                                                        Toast.makeText(v.getContext(), "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        DocumentSnapshot document = documents2.getDocuments().get(0);
                                                        String senhaArmazenada = document.getString("senha");
                                                        if (senhaValue.equals(senhaArmazenada)) {
                                                            Toast.makeText(v.getContext(), "Dados válidos", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(MainActivity.this, Home.class);
                                                            startActivity(intent);// Autenticação bem-sucedida
                                                        } else {
                                                            Toast.makeText(v.getContext(), "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    DocumentSnapshot document = documents.getDocuments().get(0);
                                    String senhaArmazenada = document.getString("senha");
                                    if (senhaValue.equals(senhaArmazenada)) {
                                        Toast.makeText(v.getContext(), "Dados válidos", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MainActivity.this, Home.class);
                                        startActivity(intent);// Autenticação bem-sucedida
                                    } else {
                                        Toast.makeText(v.getContext(), "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                Toast.makeText(v.getContext(), "Erro ao consultar o banco de dados, verifique sua internet e tente novamente.", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(v.getContext(), "Usuário ou senha incorretos." + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
            }
        });
    }
}