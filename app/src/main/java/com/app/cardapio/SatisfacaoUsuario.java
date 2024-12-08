package com.app.cardapio;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.cardapio.models.AlunoAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SatisfacaoUsuario extends AppCompatActivity {
    private FirebaseFirestore db;

    private final int[] questionIds = {
            R.id.radioGroup1,
            R.id.radioGroup2,
            R.id.radioGroup3,
            R.id.radioGroup4
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satisfacao_usuario);

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.buttonCadastrar).setOnClickListener(v -> saveFeedback());
    }

    private void saveFeedback() {
        Map<String, Object> feedback = new HashMap<>();

        try {

            Map<String, Integer> respostas = new HashMap<>();
            for (int i = 0; i < questionIds.length; i++) {
                String questionKey = "questoes" + (i + 1);
                respostas.put(questionKey, getSelectedOption(questionIds[i]));
            }

            feedback.put("respostas", respostas);
            feedback.put("dataEnvio", obterHorarioEnvio());
            feedback.put("idUsuario", obterIdUsuario());

            db.collection("pesquisas")
                    .add(feedback)
                    .addOnSuccessListener(documentReference ->
                            Toast.makeText(SatisfacaoUsuario.this, "Feedback enviado com sucesso!", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(SatisfacaoUsuario.this, "Erro ao salvar o feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } catch (IllegalStateException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int getSelectedOption(int radioGroupId) throws IllegalStateException {
        RadioGroup radioGroup = findViewById(radioGroupId);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            String questionName = getResources().getResourceEntryName(radioGroupId);
            throw new IllegalStateException("Por favor, responda a pergunta: " + questionName.replace("radioGroup", ""));
        }

        int[] buttonIds = {
                R.id.radio1_1, R.id.radio1_2, R.id.radio1_3, R.id.radio1_4, R.id.radio1_5,
                R.id.radio2_1, R.id.radio2_2, R.id.radio2_3, R.id.radio2_4, R.id.radio2_5,
                R.id.radio3_1, R.id.radio3_2, R.id.radio3_3, R.id.radio3_4, R.id.radio3_5,
                R.id.radio4_1, R.id.radio4_2, R.id.radio4_3, R.id.radio4_4, R.id.radio4_5
        };

        for (int i = 0; i < buttonIds.length; i++) {
            if (selectedId == buttonIds[i]) {
                return (i % 5) + 1;
            }
        }

        throw new IllegalStateException("Opção inválida.");
    }

    private String obterHorarioEnvio() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String obterIdUsuario() {
        return AlunoAuth.getInstance().getDocumentId();
    }
}
