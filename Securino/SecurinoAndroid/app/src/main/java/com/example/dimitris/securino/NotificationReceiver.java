package com.example.dimitris.securino;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

/*
On receive (from status service), a notification along with sound is displayed, based on the value
embedded in the intent.
*/
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the value
        int value = intent.getIntExtra("VALUE", 0);
        int notificationId = 1;
        //Build the notificaiton, sound, message, image, vibration and text
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + "com.example.dimitris.securino" + "/" + R.raw.alarm);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alert")
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setSound(soundUri);
        //Based on the value, show the corresponding message
        switch (value) {
            case 0:
                builder.setContentText("Alarm is offline or powered off.");
                break;
            case 1:
                builder.setContentText("Failed PIN try.");
                break;
            case 2:
                builder.setContentText("Movement detected.");
                break;
            case 3:
                builder.setContentText("A door/window was opened.");
                break;
            case 4:
                builder.setContentText("A sensor went offline.");
                break;
            default:
                builder.setContentText("Cause unknown.");
                break;
        }
        //Show the notification
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}
