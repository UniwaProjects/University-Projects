package com.example.dimitris.securino;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
If the boot is complete start the alarm manager to schedule the alarms.
*/
public class AlarmAutoStart extends BroadcastReceiver {
    Alarm m_StatusServiceAlarm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            m_StatusServiceAlarm = Alarm.getInstance();
            m_StatusServiceAlarm.initialize(context);
        }
    }
}
