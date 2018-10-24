package com.example.dimitris.securino;

import android.app.PendingIntent;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/*
Sets an alarm notifying itself. The time depends on the interval. Each time an alarm takes place,
the alert receiver gets called and a new alarm is scheduled.
*/
public class Alarm extends BroadcastReceiver {

    private static Alarm m_AlarmInstance = null; //Instance of the class
    private boolean m_AlarmActive;
    private final long ALARM_INTERVAL = 60000; //Since the release of API 19 repeating alarms cannot be under 60000 millis

    public Alarm() throws Exception {
        if (m_AlarmInstance == null) {
            m_AlarmActive = false;
        } else {
            throw new Exception("Instance already exist");
        }
    }

    public static Alarm getInstance() {
        if (m_AlarmInstance == null) {
            try {
                m_AlarmInstance = new Alarm();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return m_AlarmInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        //Elapsed realtime wakeup counts the time since the device was booted, its the recommended
        //way of keeping time for alarms
        //Inexact repeating lets the device synchronize wakeupo calls, resulting in fewer device wakeups,
        //hence more battery life
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                ALARM_INTERVAL, pi);
    }

    //Initializes the loop in which the alarm will call itself every given seconds.
    public void initialize(Context context) {
        if (!m_AlarmActive) {
            context = context.getApplicationContext();
            Intent i = new Intent(context.getApplicationContext(), Alarm.class);
            context.sendBroadcast(i);
            m_AlarmActive = true;
        }
    }
}
