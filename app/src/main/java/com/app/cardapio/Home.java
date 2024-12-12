package com.app.cardapio;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.cardapio.fragment.CarteiraFragment;
import com.app.cardapio.fragment.HomeFragment;
import com.app.cardapio.models.AlunoAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class Home extends AppCompatActivity {
    private boolean notificationEnabled = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Criação do canal de notificação para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "daily_notification", // ID do canal
                    "Notificações diárias", // Nome do canal
                    NotificationManager.IMPORTANCE_HIGH // Importância
            );
            channel.setDescription("Lembrete para confirmar almoço no refeitório");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configura a navegação
        BottomNavigationView navigationBar = findViewById(R.id.navigationBar);
        loadFragment(new HomeFragment());

        navigationBar.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.menu) {
                fragment = new CarteiraFragment();
            }
            return loadFragment(fragment);
        });

        // Agenda a notificação diária
        scheduleDailyNotification();
    }


    @SuppressLint("MissingPermission")
    private void scheduleDailyNotification() {
        // Verifica se a permissão é necessária para Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                // Redireciona o usuário para conceder a permissão
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        // Configura o AlarmManager para agendar a notificação
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 13);  // Define para 9h
        calendar.set(Calendar.MINUTE, 52);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,  // Repetir diariamente
                    pendingIntent
            );
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        updateNotificationIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            if (notificationEnabled) {
                showConfirmationDialog();
            } else {
                Toast.makeText(this, "Sem notificações disponíveis!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNotificationIcon() {
        if (menu != null) {
            MenuItem item = menu.findItem(R.id.action_notifications);
            item.setIcon(notificationEnabled ? R.drawable.ic_senha_on : R.drawable.ic_notificacao);
        }
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar decisão")
                .setMessage("Deseja realmente não almoçar no refeitório?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    saveResponseToFirebase(false);
                    showChangeTimeModal();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showChangeTimeModal() {
        // Exemplo simplificado de modal
        Toast.makeText(this, "Abrindo modal para alteração de horário...", Toast.LENGTH_SHORT).show();
    }

    private void saveResponseToFirebase(boolean response) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("aluno")
                .document(obterIdUsuario())
                .update("optou_almoco", response)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Resposta salva!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar resposta!", Toast.LENGTH_SHORT).show());
    }

    private String obterIdUsuario() {
        return AlunoAuth.getInstance().getDocumentId();
    }
}
