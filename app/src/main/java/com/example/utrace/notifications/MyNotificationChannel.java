package com.example.utrace.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class MyNotificationChannel {
    public static final String CHANNEL_ID = "uTrace application";
    private static final String CHANNEL_NAME = "uTrace notification";
    private static final String CHANNEL_DESC = "Green tips and milestones";

    public static void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESC);
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}

