package com.app.cardapio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.cardapio.fragment.LeitorCarteiraFragment;
import com.app.cardapio.fragment.HomeAdmFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeAdm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_adm);

        // Configura o Toolbar
        Toolbar toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

        BottomNavigationView navigationBar2 = findViewById(R.id.navigationBar2);

        // Carrega o fragment inicial
        loadFragment(new HomeAdmFragment());

        navigationBar2.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.leitorCarteira) {
                fragment = new LeitorCarteiraFragment();
            } else if (item.getItemId() == R.id.cardapio2) {
                fragment = new HomeAdmFragment();
            } else if (item.getItemId() == R.id.estatisticas) {
                fragment = new HomeAdmFragment();
            }
            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer2, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Esconde o item de notificações automaticamente na tela HomeAdm
        MenuItem notificationsItem = menu.findItem(R.id.action_notifications);
        if (notificationsItem != null) {
            notificationsItem.setVisible(false); // Esconde o botão de notificações
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_exit) {
            // Lógica para sair e redirecionar para a MainActivity
            logoutAndRedirectToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutAndRedirectToLogin() {
        SharedPreferences preferences = getSharedPreferences("appPreferences", MODE_PRIVATE);
        String savedLogin = preferences.getString("nomeUsuario", "");
        String savedPassword = preferences.getString("senhaUsuario", "");

        // Limpando o SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("userId"); // Remove a chave 'senhaUsuario'
        editor.apply();

        // Redirecionando para MainActivity e passando as credenciais
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("nomeUsuario", savedLogin);
        intent.putExtra("senhaUsuario", savedPassword);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
