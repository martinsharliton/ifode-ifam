package com.app.cardapio.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.cardapio.R;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AlunoInfoDialogFragment extends DialogFragment {

    final String id;
    private final String nome;
    private final String matricula;
    private final String curso;
    private final String campus;
    private final String qtdCreditos;
    private final String imagemUrl;
    private final OnDialogDismissListener dismissListener;

    public AlunoInfoDialogFragment(String id, String nome, String matricula, String curso,
                                   String campus, String qtdCreditos, String imagemUrl,
                                   OnDialogDismissListener dismissListener) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.curso = curso;
        this.campus = campus;
        this.qtdCreditos = qtdCreditos;
        this.imagemUrl = imagemUrl;
        this.dismissListener = dismissListener;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_aluno_info, container, false);

        // Bind views
        ImageView ivProfileImage = view.findViewById(R.id.iv_profile_image);
        TextView tvNome = view.findViewById(R.id.tv_nome);
        TextView tvMatricula = view.findViewById(R.id.tv_matricula);
        TextView tvCurso = view.findViewById(R.id.tv_curso);
        TextView tvCampus = view.findViewById(R.id.tv_campus);
        TextView tvCreditos = view.findViewById(R.id.tv_creditos);
        Button btnAceitar = view.findViewById(R.id.btn_aceitar);
        Button btnRecusar = view.findViewById(R.id.btn_recusar);

        // Set data to views
        tvNome.setText(nome);
        tvMatricula.setText("Matricula: " + matricula);
        tvCurso.setText("Curso: " + curso);
        tvCampus.setText("Campus: " + campus);
        tvCreditos.setText("Quantidade de créditos: " + qtdCreditos);

        if (imagemUrl != null && imagemUrl.contains("no_picture.png")) {
            // Carregar imagem padrão
            Glide.with(requireContext())
                    .load(R.drawable.user_app)
                    .into(ivProfileImage);
        } else {
            // Carregar a imagem da URL
            Glide.with(requireContext())
                    .load(imagemUrl) // URL da imagem
                    .into(ivProfileImage);
        }

        // Button actions
        btnAceitar.setOnClickListener(v -> descontarCreditos());

        btnRecusar.setOnClickListener(v -> dismiss());

        return view;
    }

    private void descontarCreditos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference alunoRef = db.collection("aluno").document(id);

        alunoRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                AtomicReference<Long> qtdCreditosAtual = new AtomicReference<>(documentSnapshot.getLong("qtd_creditos"));
                if (qtdCreditosAtual.get() != null && qtdCreditosAtual.get() > 0) {
                    alunoRef.update("qtd_creditos", qtdCreditosAtual.get() - 1)
                            .addOnSuccessListener(aVoid -> {
                                qtdCreditosAtual.set(documentSnapshot.getLong("qtd_creditos"));
                                Toast.makeText(getContext(), "Créditos atualizados com sucesso.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), "Novo saldo: " + qtdCreditosAtual, Toast.LENGTH_LONG).show();
                                dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Erro ao atualizar créditos.", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getContext(), "Quantidade de créditos é zero ou inválida.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Documento não encontrado.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Erro ao acessar o documento.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }

    public interface OnDialogDismissListener {
        void onDismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // Define o tamanho do diálogo
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Aplica o fundo arredondado
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
        }
    }
}
