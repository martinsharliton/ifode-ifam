package com.app.cardapio;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    boolean notificationEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configura a navegação por fragmentos
        BottomNavigationView navigationBar = findViewById(R.id.navigationBar);
        loadFragment(new HomeFragment()); // Carrega o fragmento inicial

        navigationBar.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.menu) {
                fragment = new CarteiraFragment();
            }
            return loadFragment(fragment);
        });

        // Restante do código original
        if (getIntent().getBooleanExtra("SHOW_DIALOG", false)) {
            showConfirmationDialog();
        }

        createNotificationChannel();
        scheduleDailyNotification();
        fetchNotificationStatus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            if (isWithinAllowedTime()) {
                showConfirmationDialog();
            } else {
                Toast.makeText(this, "As alterações só podem ser feitas até as 11:30.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.action_exit) {
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

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("daily_notification", "Notificações Diárias", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Lembrete para confirmar almoço no refeitório");
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    private void scheduleDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void fetchNotificationStatus() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("aluno").document(obterIdUsuario()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("optou_almoco")) {
                notificationEnabled = Boolean.TRUE.equals(documentSnapshot.getBoolean("optou_almoco"));
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao obter status!", Toast.LENGTH_SHORT).show());
    }

    private void showConfirmationDialog() {
        if (!isWithinAllowedTime()) {
            Toast.makeText(this, "As alterações só podem ser feitas até as 11:30 da manhã", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("aluno").document(obterIdUsuario()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("optou_almoco")) {
                boolean optouAlmoco = Boolean.TRUE.equals(documentSnapshot.getBoolean("optou_almoco"));
                String mensagem = optouAlmoco ? "Você escolheu não almoçar. Deseja mudar a opção?\n\n\nAs alterações só podem ser feitas até as 11:30 da manhã" : "Deseja realmente não almoçar no refeitório?\n\n\nAs alterações só podem ser feitas até as 11:30 da manhã";
                boolean novaResposta = !optouAlmoco;

                new AlertDialog.Builder(this).setTitle("Confirmar decisão").setMessage(mensagem).setPositiveButton("Confirmar", (dialog, which) -> saveResponseToFirebase(novaResposta)).setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao acessar o banco de dados.", Toast.LENGTH_SHORT).show());
    }

    private boolean isWithinAllowedTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        return (currentHour < 11 || (currentHour == 11 && currentMinute <= 30));
    }

    private void saveResponseToFirebase(boolean response) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("aluno").document(obterIdUsuario()).update("optou_almoco", response).addOnSuccessListener(aVoid -> {
            notificationEnabled = response;
            Toast.makeText(this, "Resposta salva!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar resposta!", Toast.LENGTH_SHORT).show());
    }

    private String obterIdUsuario() {
        return AlunoAuth.getInstance().getDocumentId();
    }
}
