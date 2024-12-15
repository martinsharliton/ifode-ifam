package com.app.cardapio.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cardapio.R;
import com.app.cardapio.models.Cardapio;

import java.util.List;

public class CardapioAdminAdapter extends RecyclerView.Adapter<CardapioAdminAdapter.ViewHolder> {

    public interface OnCardapioClickListener {
        void onEditCardapio(Cardapio cardapio);
        void onDeleteCardapio(Cardapio cardapio);
    }

    private List<Cardapio> cardapioList;
    private OnCardapioClickListener listener;

    public CardapioAdminAdapter(List<Cardapio> cardapioList, OnCardapioClickListener listener) {
        this.cardapioList = cardapioList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardapio_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cardapio cardapio = cardapioList.get(position);
        holder.title.setText(cardapio.getTitulo());
        holder.description.setText(cardapio.getDescricao());

        holder.editButton.setOnClickListener(v -> listener.onEditCardapio(cardapio));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteCardapio(cardapio));
    }

    @Override
    public int getItemCount() {
        return cardapioList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            description = itemView.findViewById(R.id.item_description);
            editButton = itemView.findViewById(R.id.item_edit_button);
            deleteButton = itemView.findViewById(R.id.item_delete_button);
        }
    }
}
