package com.app.cardapio.adapter;

import android.content.Context;
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
import com.app.cardapio.models.Cardapio;

import java.util.List;

public class CardapioAdminAdapter extends RecyclerView.Adapter<CardapioAdminAdapter.CardapioAdminViewHolder> { // Correção da referência ao ViewHolder
    private final List<Cardapio> itemList;
    private final Context context;

    public CardapioAdminAdapter(List<Cardapio> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CardapioAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardapio_admin, parent, false);
        return new CardapioAdminViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardapioAdminViewHolder holder, int position) { // Correção do uso do ViewHolder
        Cardapio item = itemList.get(position);

        // Configuração dos dados do item
        holder.titulo.setText(item.getTitulo());
        holder.descricao.setText(item.getDescricao());
        holder.horario.setText(item.getHorario());
        holder.imagem.setImageResource(item.getIdImagem());

        // Lógica para editar o item
        holder.editarButton.setOnClickListener(v -> {
            Toast.makeText(context, "Editar: " + item.getTitulo(), Toast.LENGTH_SHORT).show();
            Log.d("CardapioAdminAdapter", "Editar item: " + item.getTitulo());
        });


        // Lógica para excluir o item
        holder.excluirButton.setOnClickListener(v -> {
            Toast.makeText(context, "Excluir: " + item.getTitulo(), Toast.LENGTH_SHORT).show();
            Log.d("CardapioAdminAdapter", "Excluir item: " + item.getTitulo());
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CardapioAdminViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo, descricao, horario;
        public ImageView imagem;
        public Button editarButton, excluirButton;

        public CardapioAdminViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.item_title);
            descricao = itemView.findViewById(R.id.item_description);
            horario = itemView.findViewById(R.id.item_schedule);
            imagem = itemView.findViewById(R.id.item_image);
            editarButton = itemView.findViewById(R.id.item_edit_button);
            excluirButton = itemView.findViewById(R.id.item_delete_button);
        }
    }
}
