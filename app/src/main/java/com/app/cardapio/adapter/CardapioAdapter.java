package com.app.cardapio.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cardapio.R;
import com.app.cardapio.SatisfacaoUsuario;
import com.app.cardapio.models.Cardapio;

import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.CardapioViewHolder> {

    private final List<Cardapio> itemList;
    private final Context context;

    public CardapioAdapter(List<Cardapio> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CardapioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardapio, parent, false);
        return new CardapioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardapioViewHolder holder, int position) {
        Cardapio item = itemList.get(position);

        holder.titulo.setText(item.getTitulo());
        holder.descricao.setText(item.getDescricao());
        holder.horario.setText(item.getHorario());
        holder.imagem.setImageResource(item.getIdImagem());

        holder.avaliarRestauranteButton.setOnClickListener(v -> {
            if (context != null) {
                try {
                    Log.d("CardapioAdapter", "Botão clicado para: " + item.getTitulo());
                    Toast.makeText(context, "Abrindo avaliação para: " + item.getTitulo(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, SatisfacaoUsuario.class);
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("CardapioAdapter", "Contexto é nulo ao clicar no botão Avaliar Restaurante.");
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

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
