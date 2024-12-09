package com.app.cardapio;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.app.cardapio.fragment.CarteiraFragment;
import com.app.cardapio.fragment.HomeFragment;
import com.app.cardapio.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class homeAdm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_adm);

    }
}