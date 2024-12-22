package com.app.cardapio.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.cardapio.R;
import com.app.cardapio.models.AlunoAuth;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class CarteiraFragment extends Fragment {

    private TextView tvNome, tvMatricula, tvCurso, tvCampus, tvCreditos;
    ImageView ivprofileImage, imageQrcode;
    private ListenerRegistration listenerRegistration;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carteira, container, false);

        tvNome = view.findViewById(R.id.profileNamecard);
        tvMatricula = view.findViewById(R.id.numeroMatriculacard);
        tvCurso = view.findViewById(R.id.nomeCursocard);
        tvCampus = view.findViewById(R.id.nomeCampuscard);
        tvCreditos = view.findViewById(R.id.qtd_creditos);
        ivprofileImage = view.findViewById(R.id.profileImage);
        imageQrcode = view.findViewById(R.id.imageQrcode);

        // Instância do Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtendo o ID do aluno (usado para recuperar os dados)
        String userId = AlunoAuth.getInstance().getDocumentId();

        DocumentReference alunoRef = db.collection("aluno").document(userId);

        // Adiciona um listener ao documento para monitorar mudanças em tempo real
        listenerRegistration = alunoRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(getActivity(), "Erro ao monitorar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Atualizar os TextViews com os dados do aluno
                String nome = documentSnapshot.getString("nome");
                String matricula = documentSnapshot.getString("matricula");
                String curso = documentSnapshot.getString("curso");
                String campus = documentSnapshot.getString("campus");

                // Verifique o tipo de "qtd_creditos" e converta para String
                Object qtdCreditosObj = documentSnapshot.get("qtd_creditos");
                String qtdCreditos = qtdCreditosObj != null ? qtdCreditosObj.toString() : "0";

                String imagemUrl = documentSnapshot.getString("foto");

                // Atualizando a UI com as informações do aluno
                tvNome.setText(nome);
                tvMatricula.setText("Matricula: " + matricula);
                tvCurso.setText("Curso: " + curso);
                tvCampus.setText("Campus: " + campus);
                tvCreditos.setText("Quantidade de créditos: " + qtdCreditos + " restantes");

                // Carregar a imagem de perfil usando Glide
                if (imagemUrl != null && imagemUrl.contains("no_picture.png")) {
                    // Carregar imagem padrão
                    Glide.with(requireContext())
                            .load(R.drawable.user_app)
                            .into(ivprofileImage);
                } else {
                    // Carregar a imagem da URL
                    Glide.with(requireContext())
                            .load(imagemUrl) // URL da imagem
                            .into(ivprofileImage);
                }
            } else {
                // Caso o documento não exista
                Toast.makeText(getActivity(), "Dados não encontrados.", Toast.LENGTH_SHORT).show();
            }
        });

        // Dados para o QR Code
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(userId, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
                }
            }
            imageQrcode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remover o listener quando a fragment for destruída
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
