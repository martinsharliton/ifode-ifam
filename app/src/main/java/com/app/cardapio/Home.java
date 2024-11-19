package com.app.cardapio;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cardapio.adapter.CardapioAdapter;
import com.app.cardapio.models.Cardapio;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicialize os dados
        List<Cardapio> cardapioList = new ArrayList<>();

        cardapioList.add(new Cardapio("Lasanha e Carne Moída",
                "Alimentos saudáveis são fundamentais para manter o corpo e a mente funcionando bem.",
                "Segunda-feira - 11h às 13h",
                R.drawable.logo_ifam));

        cardapioList.add(new Cardapio("Lasanha e Carne Moída",
                "Alimentos saudáveis são fundamentais para manter o corpo e a mente funcionando bem.",
                "Terça-feira - 11h às 13h",
                R.drawable.logo_ifam));

        cardapioList.add(new Cardapio("Lasanha e Carne Moída",
                "Alimentos saudáveis são fundamentais para manter o corpo e a mente funcionando bem.",
                "Quarta-feira - 11h às 13h",
                R.drawable.logo_ifam));

        cardapioList.add(new Cardapio("Lasanha e Carne Moída",
                "Alimentos saudáveis são fundamentais para manter o corpo e a mente funcionando bem.",
                "Quinta-feira - 11h às 13h",
                R.drawable.logo_ifam));

        // Configurar o Adapter
        CardapioAdapter adapter = new CardapioAdapter(cardapioList);
        recyclerView.setAdapter(adapter);

    }
}
