package com.example.dimitris.securino;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
On receive (from Alarm class) the alert service is called.
*/
public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, StatusService.class);
        context.startService(i);
    }
}
