package com.example.solarcalculator.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class GoldenTimeNotificationService extends JobIntentService {


    private static final int JOB_ID = 0x01;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GoldenTimeNotificationService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        startMyOwnForeground();


    }

    @SuppressLint("NewApi")
    private void startMyOwnForeground() {


        String NOTIFICATION_CHANNEL_ID = "NotificationChannel1";
        String channelName = "Notification Channel 1";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (manager != null) {
            List<NotificationChannel> channelList = manager.getNotificationChannels();

            for (int i = 0; channelList != null && i < channelList.size(); i++) {
            }
        }


        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(false)
                .setContentTitle("Golden Hour Started")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        try {
            this.startForeground(1, notification);
            //stopForeground(true);
        } catch (Exception e) {
        }
    }

}
