package com.app.cardapio.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cardapio.R;
import com.app.cardapio.adapter.CardapioAdminAdapter;
import com.app.cardapio.models.Cardapio;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeAdmFragment extends Fragment implements CardapioAdminAdapter.OnCardapioClickListener {

    private RecyclerView recyclerView;
    private CardapioAdminAdapter adapter;
    private List<Cardapio> cardapioList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_adm, container, false);

        recyclerView = view.findViewById(R.id.recycleradmView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button criarCardapio = view.findViewById(R.id.criarCardapio);
        criarCardapio.setOnClickListener(v -> openCadastrarCardapioDialog(null));

        cardapioList = new ArrayList<>();
        adapter = new CardapioAdminAdapter(cardapioList, this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
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
            if (querySnapshot != null) {
                for (DocumentSnapshot doc : querySnapshot) {
                    String id = doc.getId();
                    String titulo = doc.getString("titulo");
                    String descricao = doc.getString("descricao");
                    String horario = doc.getString("horario");
                    cardapioList.add(new Cardapio(id, titulo, descricao, horario, R.drawable.logo_ifam));
                }
            }
            adapter.notifyDataSetChanged(); // Notifica o adapter sobre a mudança na lista
        });
    }


    private void openCadastrarCardapioDialog(Cardapio cardapio) {
        CadastrarCardapioDialog dialog = CadastrarCardapioDialog.newInstance(cardapio);
        dialog.show(getParentFragmentManager(), "CadastrarCardapioDialog");
    }

    @Override
    public void onEditCardapio(Cardapio cardapio) {
        openCadastrarCardapioDialog(cardapio);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDeleteCardapio(Cardapio cardapio) {
        db.collection("cardapios").document(cardapio.getId()).delete().addOnSuccessListener(aVoid -> {
            cardapioList.remove(cardapio);
            adapter.notifyDataSetChanged();
        });
    }
}
