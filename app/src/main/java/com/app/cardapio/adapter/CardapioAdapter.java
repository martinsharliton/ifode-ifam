package com.app.cardapio.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cardapio.R;
import com.app.cardapio.models.Cardapio;

import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.CardapioViewHolder> {

    private final List<Cardapio> itemList;

    // Construtor
    public CardapioAdapter(List<Cardapio> itemList) {
        this.itemList = itemList;
    }

    // Cria a ViewHolder
    @NonNull
    @Override
    public CardapioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cardapio, parent, false);
        return new CardapioViewHolder(itemView);
    }

    // Popula os dados na ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CardapioViewHolder holder, int position) {
        Cardapio item = itemList.get(position);
        holder.titulo.setText(item.getTitulo());
        holder.descricao.setText(item.getDescricao());
        holder.horario.setText(item.getHorario());
        holder.imagem.setImageResource(item.getIdImagem());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder
    public static class CardapioViewHolder extends RecyclerView.ViewHolder {

        public TextView titulo, descricao, horario;
        public ImageView imagem;
        public Button avaliarRestauranteButton;

        public CardapioViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.item_title);
            descricao = itemView.findViewById(R.id.item_description);
            horario = itemView.findViewById(R.id.item_schedule);
            imagem = itemView.findViewById(R.id.item_image);
            avaliarRestauranteButton = itemView.findViewById(R.id.item_button);
        }
    }
}

