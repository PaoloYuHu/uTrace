package com.example.utrace.notifications;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.utrace.R;

public class NotificationReceiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("notification_title");
        String message = intent.getStringExtra("notification_message");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyNotificationChannel.CHANNEL_ID)
                .setSmallIcon(R.drawable.utrace_miniature_logo)
                .setContentTitle(title != null ? title : "Default Title")
                .setContentText(message != null ? message : "Default Message")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}


