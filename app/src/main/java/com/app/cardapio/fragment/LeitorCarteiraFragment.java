package com.app.cardapio.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.cardapio.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class LeitorCarteiraFragment extends Fragment {

    private DecoratedBarcodeView barcodeScannerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leitor_carteira, container, false);

        barcodeScannerView = view.findViewById(R.id.barcode_scanner);
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result != null) {
                    String qrCodeData = result.getText();
                    fetchAlunoData(qrCodeData);
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Pode ser usado para feedback visual
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        barcodeScannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScannerView.pause();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            barcodeScannerView.resume();
        }
    }

    private void fetchAlunoData(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference alunoRef = db.collection("aluno").document(documentId);

        alunoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nome = document.getString("nome");
                    String matricula = document.getString("matricula");
                    String curso = document.getString("curso");
                    String campus = document.getString("campus");
                    Object qtdCreditosObj = document.get("qtd_creditos");
                    String qtdCreditos = qtdCreditosObj != null ? qtdCreditosObj.toString() : "0";
                    String imagemUrl = document.getString("foto");

                    // Show dialog with data
                    AlunoInfoDialogFragment dialog = new AlunoInfoDialogFragment(
                            nome, matricula, curso, campus, qtdCreditos, imagemUrl);
                    dialog.show(getParentFragmentManager(), "AlunoInfoDialog");
                } else {
                    Toast.makeText(getContext(), "Documento não encontrado no Firebase.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Erro ao buscar dados: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
