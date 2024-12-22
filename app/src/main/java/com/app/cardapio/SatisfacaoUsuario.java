package com.app.cardapio;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.app.cardapio.models.AlunoAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SatisfacaoUsuario extends AppCompatActivity {
    private FirebaseFirestore db;

    // IDs dos layouts das perguntas
    private final int[] layoutIds = {R.id.layoutQuestion1, R.id.layoutQuestion2, R.id.layoutQuestion3, R.id.layoutQuestion4};

    // Mapa para armazenar as respostas selecionadas
    private final Map<String, Integer> respostas = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Se estiver em modo escuro, forçar modo claro
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_satisfacao_usuario);

        // Configurar o ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Pesquisa"); // Define o título
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.primary))); // Fundo da barra
            actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar seta de voltar

            // Definir cor do texto para branco
            SpannableString spannableTitle = new SpannableString("Pesquisa");
            spannableTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.white)), 0, spannableTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(spannableTitle);

            // Definir cor da seta de voltar para branco
            actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        }


        db = FirebaseFirestore.getInstance();

        setupImageClickListeners();

        findViewById(R.id.buttonCadastrar).setOnClickListener(v -> saveFeedback());
    }

    private void setupImageClickListeners() {
        // Configura os cliques nas imagens para cada pergunta
        for (int i = 0; i < layoutIds.length; i++) {
            int questionNumber = i + 1;
            LinearLayout layout = findViewById(layoutIds[i]);

            for (int j = 0; j < layout.getChildCount(); j++) {
                ImageView imageView = (ImageView) layout.getChildAt(j);
                final int selectedValue = j + 1; // Valores de 1 a 5

                imageView.setOnClickListener(v -> {
                    resetSelection(layout); // Remove a seleção anterior
                    imageView.setForeground(ContextCompat.getDrawable(this, R.drawable.selected_background)); // Aplica efeito visual
                    respostas.put("questoes" + questionNumber, selectedValue);
                });
            }
        }
    }

    private void resetSelection(LinearLayout layout) {
        // Remove o foreground selecionado de todas as imagens
        for (int i = 0; i < layout.getChildCount(); i++) {
            layout.getChildAt(i).setForeground(null);
        }
    }

    private void saveFeedback() {
        try {
            // Verifica se todas as perguntas foram respondidas
            for (int i = 1; i <= layoutIds.length; i++) {
                if (!respostas.containsKey("questoes" + i)) {
                    throw new IllegalStateException("Por favor, responda a pergunta: " + i);
                }
            }

            Map<String, Object> feedback = new HashMap<>();
            feedback.put("respostas", respostas);
            feedback.put("dataEnvio", obterHorarioEnvio());
            feedback.put("idUsuario", obterIdUsuario());

            db.collection("pesquisas").add(feedback).addOnSuccessListener(documentReference -> {
                Toast.makeText(SatisfacaoUsuario.this, "Feedback enviado com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Home.class));
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(SatisfacaoUsuario.this, "Erro ao salvar o feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Home.class));
                finish();
            });
        } catch (IllegalStateException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String obterHorarioEnvio() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", new Locale("pt", "BR"));
        return sdf.format(new Date());
    }

    private String obterIdUsuario() {
        return AlunoAuth.getInstance().getDocumentId();
    }

    // Gerenciar o clique na seta de voltar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Voltar para a tela anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
