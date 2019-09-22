package com.example.solarcalculator.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.solarcalculator.R;
import com.example.solarcalculator.services.GoldenTimeNotificationService;
import com.mmi.util.LogUtils;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "ALARM", Toast.LENGTH_LONG).show();
        Log.e("Alarm ", "");

        Intent intentalarm = new Intent(context, GoldenTimeNotificationService.class);
        intent.putExtra("Work", "Alarm");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            GoldenTimeNotificationService.enqueueWork(context, intentalarm);
        } else {
            context.startService(intentalarm);
        }
    }





}