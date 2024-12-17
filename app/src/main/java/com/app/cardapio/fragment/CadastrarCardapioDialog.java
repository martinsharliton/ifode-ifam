package com.app.cardapio.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.cardapio.R;
import com.app.cardapio.models.Cardapio;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Objects;

public class CadastrarCardapioDialog extends DialogFragment {

    private EditText editTextTitle, editTextDescription;
    private Spinner spinnerHorario;
    Button saveButton;
    private FirebaseFirestore db;

    private Cardapio cardapio;

    public static CadastrarCardapioDialog newInstance(Cardapio cardapio) {
        CadastrarCardapioDialog dialog = new CadastrarCardapioDialog();
        Bundle args = new Bundle();
        args.putSerializable("cardapio", (Serializable) cardapio);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastrar_cardapio, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerHorario = view.findViewById(R.id.spinnerHorario);
        saveButton = view.findViewById(R.id.buttonSaveCardapio);

        db = FirebaseFirestore.getInstance();

        // Configurando o ArrayAdapter para o Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.horarios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHorario.setAdapter(adapter);
        spinnerHorario.setPopupBackgroundResource(android.R.color.white);

        if (getArguments() != null) {
            cardapio = (Cardapio) getArguments().getSerializable("cardapio");
            if (cardapio != null) {
                editTextTitle.setText(cardapio.getTitulo());
                editTextDescription.setText(cardapio.getDescricao());
            }
        }

        saveButton.setOnClickListener(v -> saveCardapio());
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ajustando o tamanho do diálogo para ocupar a largura da tela
        if (getDialog() != null) {
            Objects.requireNonNull(getDialog().getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null) {
            // Ajustando o tamanho do diálogo para ocupar toda a largura da tela
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            // Aplica o tamanho e o background arredondado
            Objects.requireNonNull(getDialog().getWindow()).setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
        }
    }

    private void saveCardapio() {
        try {
            String id = (cardapio != null) ? cardapio.getId() : db.collection("cardapios").document().getId();
            String titulo = editTextTitle.getText().toString();
            String descricao = editTextDescription.getText().toString();
            String horario = spinnerHorario.getSelectedItem().toString();
            int defaultImage = R.drawable.logo_ifam;

            if (cardapio == null) {
                db.collection("cardapios").add(new Cardapio(id, titulo, descricao, horario, defaultImage));
            } else {
                db.collection("cardapios").document(cardapio.getId())
                        .update("titulo", titulo, "descricao", descricao, "horario", horario);
            }
            Toast.makeText(getContext(), "Cardápio salvo com sucesso.", Toast.LENGTH_LONG).show();
            dismiss();  // Fecha o diálogo
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao salvar cardápio: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
