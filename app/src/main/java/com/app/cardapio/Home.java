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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Home extends AppCompatActivity {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    boolean notificationEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getBooleanExtra("SHOW_DIALOG", false)) {
            showConfirmationDialog();
        }

        BottomNavigationView navigationBar = findViewById(R.id.navigationBar);
        toolbar.setSubtitle("Cardápio");
        loadFragment(new HomeFragment());

        navigationBar.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.home) {
                toolbar.setSubtitle("Cardápio");
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.menu) {
                toolbar.setSubtitle("Minha Carteirinha");
                fragment = new CarteiraFragment();
            }
            return loadFragment(fragment);
        });

        requestNotificationPermission();

        createNotificationChannel();
        scheduleDailyNotification();
        fetchNotificationStatus();
    }

    private void updateNotificationIconBasedOnTime(Menu menu) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        boolean isBetween9And1130 = (currentHour == 9 || currentHour == 10 || (currentHour == 11 && currentMinute <= 30));

        MenuItem item = menu.findItem(R.id.action_notifications);
        if (item != null) {
            item.setIcon(isBetween9And1130 ? R.drawable.ic_notification_with_badge : R.drawable.ic_notificacao);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
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

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("userId");
        editor.apply();

        Intent intent = new Intent(this, Login.class);
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

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

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

    private void saveResponseToFirebase(boolean response) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = obterIdUsuario();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        String documentPath = "respostas/" + userId + "_" + currentDate;

        db.document(documentPath).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (isWithinAllowedTime()) {
                    updateResponseInCollection(db, documentPath, response);
                } else {
                    Toast.makeText(this, "Fora do horário permitido. Resposta não pode ser atualizada.", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (isWithinAllowedTime()) {
                    createNewResponseInCollection(db, documentPath, userId, currentDate, response);
                } else {
                    Toast.makeText(this, "Fora do horário permitido. Resposta não pode ser criada.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao verificar resposta existente.", Toast.LENGTH_SHORT).show());
    }

    private void updateResponseInCollection(FirebaseFirestore db, String documentPath, boolean response) {
        db.document(documentPath).update("response", response, "timestamp", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> {
                    notificationEnabled = response;
                    Toast.makeText(this, "Resposta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao atualizar resposta.", Toast.LENGTH_SHORT).show());
    }

    private void createNewResponseInCollection(FirebaseFirestore db, String documentPath, String userId, String currentDate, boolean response) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("date", currentDate);
        data.put("response", response);
        data.put("timestamp", FieldValue.serverTimestamp());

        db.document(documentPath).set(data)
                .addOnSuccessListener(aVoid -> {
                    notificationEnabled = response;
                    Toast.makeText(this, "Resposta salva com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar resposta.", Toast.LENGTH_SHORT).show());
    }

    private boolean isWithinAllowedTime() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        return (currentHour == 9 || currentHour == 10 || (currentHour == 11 && currentMinute <= 30));
    }

    private String obterIdUsuario() {
        return AlunoAuth.getInstance().getDocumentId();
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

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Confirmar decisão")
                        .setMessage(mensagem)
                        .setPositiveButton("Confirmar", (dialogInterface, which) -> saveResponseToFirebase(novaResposta))
                        .setNegativeButton("Cancelar", (dialogInterface, which) -> dialogInterface.dismiss()).create();

                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.dialog_rounded_background);

                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.primary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.primary));

            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Erro ao acessar o banco de dados.", Toast.LENGTH_SHORT).show());
    }
}
