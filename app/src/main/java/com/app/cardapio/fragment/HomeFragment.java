package com.app.cardapio.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.appcompat.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cardapio.R;
import com.app.cardapio.adapter.CardapioAdapter;
import com.app.cardapio.models.Cardapio;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    private TextView mensagem;
    private CardapioAdapter adapter;
    private List<Cardapio> cardapioList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // Configuração do RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        mensagem = view.findViewById(R.id.textView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializando Firebase e Adapter
        db = FirebaseFirestore.getInstance();
        cardapioList = new ArrayList<>();
        adapter = new CardapioAdapter(cardapioList, getContext());
        recyclerView.setAdapter(adapter);

        // Buscar dados do Firestore
        loadCardapiosFromFirestore();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCardapiosFromFirestore() {
        db.collection("cardapios").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Erro ao carregar cardápios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            cardapioList.clear(); // Limpa a lista para evitar duplicação
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                for (DocumentSnapshot doc : querySnapshot) {
                    String id = doc.getId();
                    String titulo = doc.getString("titulo");
                    String descricao = doc.getString("descricao");
                    String horario = doc.getString("horario");

                    int imagemId = getImageResourceId();
                    cardapioList.add(new Cardapio(id, titulo, descricao, horario, imagemId));
                }
            }

            // Atualiza a mensagem se não houver cardápios
            if (cardapioList.isEmpty()) {
                mensagem.setText("Cardápio desta semana ainda não cadastrado");
            } else {
                mensagem.setText("Cardápio"); // Limpa a mensagem se houver cardápios
            }

            adapter.notifyDataSetChanged(); // Notifica o Adapter para atualizar a RecyclerView
        });
    }

    // Função para mapear o nome da imagem no Firestore para o ID do drawable
    private int getImageResourceId() {
        return R.drawable.logo_ifam; // Imagem padrão
    }
}
