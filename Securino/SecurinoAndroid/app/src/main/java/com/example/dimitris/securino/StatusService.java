package com.example.dimitris.securino;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.util.concurrent.TimeUnit;

/*
This service, runs in the background for just enough time to receive the variables and issue a
notification. If the device is offline no action takes place.
*/
public class StatusService extends IntentService {
    UbidotsStatus m_UbidotsStatus;

    public StatusService() {
        super("test-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isOnline()) {
            //Gets the instance of ubidots status, receives the status and waits until the received flag
            //turns to true
            m_UbidotsStatus = UbidotsStatus.getInstance();
            synchronized (this) {
                m_UbidotsStatus.receiveStatus();
                try {
                    this.wait(50);
                    while (!m_UbidotsStatus.m_NotificationSyncFlag) {
                        this.wait(500);
                    }
                    m_UbidotsStatus.m_NotificationSyncFlag = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    android.util.Log.i("Info", "Waiting Error");
                }
            }
            android.util.Log.i("Info", "Status Service: " + Thread.currentThread().getId());
            //Then proceeds to send the appropriate notifications if there is any need to do so.
            long lastOnline = m_UbidotsStatus.getTimestamp();
            long offlineTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastOnline);
            if (offlineTime > m_UbidotsStatus.OFFLINE_MAX_MINS) {
                sendNotification(0);
            } else {
                if (m_UbidotsStatus.getState() == Status.STATE_ALERT) {
                    if (m_UbidotsStatus.getSensor() == Status.SENSOR_MAGNET_TRIGGERED) {
                        sendNotification(2);
                    } else if (m_UbidotsStatus.getSensor() == Status.SENSOR_PIR_TRIGGERED) {
                        sendNotification(3);
                    } else if (m_UbidotsStatus.getSensor() == Status.SENSOR_OFFLINE) {
                        sendNotification(4);
                    } else {
                        sendNotification(1);
                    }
                }
            }
        }
    }

    //Returns true if the phone is connected to the network
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    //Sends the notifcation to the notifcation receiver, along with the number of the
    //notification to be displayed
    private void sendNotification(int value) {
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        intent.putExtra("VALUE", value);
        sendBroadcast(intent);
    }
}
