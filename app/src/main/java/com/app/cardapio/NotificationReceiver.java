package com.app.cardapio;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Verifica a permissão POST_NOTIFICATIONS no Android 13+ (API 33+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // Intent para abrir a atividade Home com o modal
        Intent activityIntent = new Intent(context, Home.class);
        activityIntent.putExtra("SHOW_DIALOG", true);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Configura o PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Configura a notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "daily_notification")
                .setSmallIcon(R.drawable.ic_notificacao) // Ícone da notificação
                .setContentTitle("Lembrete de Almoço")
                .setContentText("Não esqueça de confirmar seu almoço no refeitório!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Fecha a notificação ao clicar
                .setContentIntent(pendingIntent); // Associa o PendingIntent

        // Exibe a notificação
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, builder.build()); // Substitua "1" por um ID dinâmico, se necessário
    }
}
