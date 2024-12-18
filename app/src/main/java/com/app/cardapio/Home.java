package com.app.cardapio;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.cardapio.fragment.CarteiraFragment;
import com.app.cardapio.fragment.HomeFragment;
import com.app.cardapio.models.AlunoAuth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class Home extends AppCompatActivity {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    boolean notificationEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Se estiver em modo escuro, forçar modo claro
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_home);

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getBooleanExtra("SHOW_DIALOG", false)) {
            showConfirmationDialog();
        }

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

        // Solicitar permissões para notificações
        requestNotificationPermission();

        if (getIntent().getBooleanExtra("SHOW_DIALOG", false)) {
            showConfirmationDialog();
        }

        createNotificationChannel();
        scheduleDailyNotification();
        fetchNotificationStatus();
    }

    private void updateNotificationIconBasedOnTime(Menu menu) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Verifica se o horário está entre 9:00 e 11:30
        boolean isBetween9And1130 = (currentHour == 9 || currentHour == 10 || (currentHour == 11 && currentMinute <= 30));

        // Encontra o item no menu
        MenuItem item = menu.findItem(R.id.action_notifications);
        if (item != null) {
            // Atualiza o ícone com base no horário
            item.setIcon(isBetween9And1130 ? R.drawable.ic_notification_with_badge : R.drawable.ic_notificacao);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Atualizar o ícone após o menu ser inflado
        updateNotificationIconBasedOnTime(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            if (isWithinAllowedTime()) {
                showConfirmationDialog();
            } else {
                Toast.makeText(this, "As alterações só podem ser feitas entre as 09:00 e 11:30 da manhã.", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == R.id.action_exit) {
            // Lógica para sair e redirecionar para a MainActivity
            logoutAndRedirectToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        }
        return false;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Checar se a permissão já foi concedida
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de notificações concedida!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissão de notificações negada!", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel("daily_notification", "Notificações Diárias", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Lembrete para confirmar almoço no refeitório");
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public void scheduleDailyNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancelar qualquer alarme existente antes de agendar um novo
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        // Configurar horário para 9:00 da manhã
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Se o horário de hoje já passou, agendar para amanhã
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Agendar o alarme diário
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
            Toast.makeText(this, "As alterações só podem ser feitas entre as 09:00 e 11:30 da manhã.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("aluno").document(obterIdUsuario()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("optou_almoco")) {
                boolean optouAlmoco = Boolean.TRUE.equals(documentSnapshot.getBoolean("optou_almoco"));
                String mensagem = optouAlmoco ? "Você escolheu não almoçar. Deseja mudar a opção?\n\n\nAs alterações só podem ser feitas entre as 09:00 e 11:30 da manhã." : "Deseja realmente não almoçar no refeitório?\n\n\nAs alterações só podem ser feitas entre as 09:00 e 11:30 da manhã.";
                boolean novaResposta = !optouAlmoco;

                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Confirmar decisão").setMessage(mensagem).setPositiveButton("Confirmar", (dialogInterface, which) -> saveResponseToFirebase(novaResposta)).setNegativeButton("Cancelar", (dialogInterface, which) -> dialogInterface.dismiss()).create();

                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_rounded_background);

                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.primary));

            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao acessar o banco de dados.", Toast.LENGTH_SHORT).show());
    }

    private boolean isWithinAllowedTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        return (currentHour == 3 || currentHour == 13 || (currentHour == 11 && currentMinute <= 30));
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
