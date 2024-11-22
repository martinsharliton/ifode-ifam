package com.app.cardapio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cardapio.R;
import com.app.cardapio.adapter.CardapioAdapter;
import com.app.cardapio.models.Cardapio;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lista de itens do cardápio
        List<Cardapio> cardapioList = new ArrayList<>();
        cardapioList.add(new Cardapio("Lasanha e Carne Moída", "Alimentos saudáveis são fundamentais.", "Segunda-feira - 11h às 13h", R.drawable.logo_ifam));
        cardapioList.add(new Cardapio("Frango Assado", "Nutrição balanceada para o dia.", "Terça-feira - 11h às 13h", R.drawable.logo_ifam));
        cardapioList.add(new Cardapio("Peixe Grelhado", "Refeição leve e saudável.", "Quarta-feira - 11h às 13h", R.drawable.logo_ifam));

        // Configurar o Adapter
        CardapioAdapter adapter = new CardapioAdapter(cardapioList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
