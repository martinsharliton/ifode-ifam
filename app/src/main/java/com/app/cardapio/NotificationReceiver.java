package com.app.cardapio;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @SuppressLint({"MissingPermission", "NotificationPermission"})
    @Override
    public void onReceive(Context context, Intent intent) {
        // Verifica se o Intent recebido possui a ação correta
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Criação da notificação
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "daily_notification")
                    .setSmallIcon(R.drawable.logo_ifam)
                    .setContentTitle("Refeitório")
                    .setContentText("Deseja não almoçar hoje? Clique aqui para decidir.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            // Envio da notificação
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            manager.notify(1, builder.build());
        }
    }
}
