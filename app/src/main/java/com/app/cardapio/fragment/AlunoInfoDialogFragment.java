package com.app.cardapio.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.cardapio.R;
import com.bumptech.glide.Glide;

public class AlunoInfoDialogFragment extends DialogFragment {

    private String nome, matricula, curso, campus, qtdCreditos, imagemUrl;

    public AlunoInfoDialogFragment(String nome, String matricula, String curso,
                                   String campus, String qtdCreditos, String imagemUrl) {
        this.nome = nome;
        this.matricula = matricula;
        this.curso = curso;
        this.campus = campus;
        this.qtdCreditos = qtdCreditos;
        this.imagemUrl = imagemUrl;
    }

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

        // Load image with Glide
        Glide.with(requireContext())
                .load(imagemUrl)
                .placeholder(R.drawable.ic_person)
                .into(ivProfileImage);

        // Button actions
        btnAceitar.setOnClickListener(v -> {
            dismiss();
            // Handle accept action here
        });

        btnRecusar.setOnClickListener(v -> {
            dismiss();
            // Handle refuse action here
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
