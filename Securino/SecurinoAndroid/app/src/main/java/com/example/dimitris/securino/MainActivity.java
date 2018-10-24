package com.example.dimitris.securino;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/*
The main activity is responsible for the UI and the changes that happen to it.
It exchanges status with Ubidots only on user's demand and the UI checks every second for a change
but refreshes only when a new status is receives, through the use of a flag.
*/
public class MainActivity extends Activity {

    private ImageButton m_StatusButton;
    private TextView m_TimestampText;
    private TextView m_ArmMethodText;
    private UbidotsStatus m_UbidotsStatus;
    private Timer m_UpdateUITimer;
    private Alarm m_StatusServiceAlarm;
    private ProgressDialog m_ProgressDialog;

    //Initiliaze visual elements and start the alarm service
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_StatusButton = (ImageButton) findViewById(R.id.statusImageButton);
        m_StatusButton.setOnClickListener(armButtonListener);
        m_TimestampText = (TextView) findViewById(R.id.timestampTextView);
        m_ArmMethodText = (TextView) findViewById(R.id.armMethodTextView);
        m_UbidotsStatus = UbidotsStatus.getInstance();
        m_StatusServiceAlarm = Alarm.getInstance();
        m_StatusServiceAlarm.initialize(this);
        m_ProgressDialog = new ProgressDialog(this);
        m_ProgressDialog.setCancelable(false);
        m_ProgressDialog.setMessage("Connecting to server. Please wait...");
        ImageButton m_RefreshButton = (ImageButton) findViewById(R.id.refreshImageButton);
        m_RefreshButton.setOnClickListener(refreshButtonListener);
    }

    //On resume, receive the recent status and create a new timer for the UI.
    //Then update the UI.
    @Override
    protected void onResume() {
        super.onResume();
        receiveStatus();
        m_UpdateUITimer = new Timer();
        updateUI();
    }

    //On pause cancel the timer for the UI update.
    @Override
    protected void onPause() {
        super.onPause();
        m_UpdateUITimer.cancel();
    }

    //If there is an internet connection available, receive a new status.
    private void receiveStatus() {
        if (isOnline()) {
            m_ProgressDialog.show();
            m_UbidotsStatus.receiveStatus();
        }
    }

    //If the status flag is true, redraw the corresponding elements of the UI. This asynchronously
    //through the help of a handler every 1 second.
    private void updateUI() {
        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (m_UbidotsStatus.m_UISyncFlag) {
                                m_UbidotsStatus.m_UISyncFlag = false;
                                drawUI();
                                m_ProgressDialog.dismiss();
                            } else if (!isOnline()) {
                                drawUI();
                            }
                        } catch (Exception e) {
                            android.util.Log.i("Error", "Update UI Error");
                        }
                    }
                });
            }
        };
        m_UpdateUITimer.schedule(doAsynchronousTask, 0, 1000);
    }

    //Redraw the elements that are relevant to the status.
    private void drawUI() {
        if (isOnline()) {
            //Set timestamp text
            Date lastDate = new Date(m_UbidotsStatus.getTimestamp());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
            m_TimestampText.setText("Changed: " + sdf.format(lastDate));
            //If the alarm is online
            long offlineTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - m_UbidotsStatus.getTimestamp());
            if (offlineTime > m_UbidotsStatus.OFFLINE_MAX_MINS) {
                m_StatusButton.setBackgroundResource(R.drawable.lock);
                m_ArmMethodText.setText("Alarm is offline.");
            }
            //If it is, update the interface and set the last updateded date.
            else {
                //Set arm button image
                if (m_UbidotsStatus.getState() == Status.STATE_DISARMED) {
                    m_StatusButton.setBackgroundResource(R.drawable.unlocked);
                } else if (m_UbidotsStatus.getState() == Status.STATE_ARMED) {
                    m_StatusButton.setBackgroundResource(R.drawable.locked);
                } else if (m_UbidotsStatus.getState() == Status.STATE_ALERT) {
                    m_StatusButton.setBackgroundResource(R.drawable.siren);
                }
                //Set arm method text
                if (m_UbidotsStatus.getMethod() == Status.METHOD_NO_ARM) {
                    m_ArmMethodText.setText("");
                } else if (m_UbidotsStatus.getMethod() == Status.METHOD_ARM_STAY) {
                    m_ArmMethodText.setText("Arm Method: Arm Stay");
                } else if (m_UbidotsStatus.getMethod() == Status.METHOD_ARM_AWAY) {
                    m_ArmMethodText.setText("Arm Method: Arm Away");
                }
            }
        }
        //Else display that the phone is not connected to the internet.
        else {
            m_StatusButton.setBackgroundResource(R.drawable.lock);
            m_TimestampText.setText("");
            m_ArmMethodText.setText("No internet connectivity.");
        }
    }

    //Returns true if the phone is connected to the network
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    //Listener for the arm button, if the status is disabled during the press, a popup menu is
    //displayed, letting the user choose the arm method. Otherwise the state is disabled. In any case
    //the status is changed, a request to the site is sent and a redraw of the UI is called.
    //If the phone or the alarm is offline, this button is disabled.
    private OnClickListener armButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            long offlineTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - m_UbidotsStatus.getTimestamp());
            if (isOnline() && (offlineTime < m_UbidotsStatus.OFFLINE_MAX_MINS)) {
                final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                if (m_UbidotsStatus.getState() == Status.STATE_DISARMED) {
                    String armMethodsArray[] = {"Arm Stay", "Arm Away"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Select Arm Method").setItems(armMethodsArray, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            vibe.vibrate(100);
                            if (which == 0) {
                                Status newStatus = new Status();
                                newStatus.setStatusArmedStay();
                                m_UbidotsStatus.sendStatus(newStatus);
                                receiveStatus();
                            } else {
                                Status newStatus = new Status();
                                newStatus.setStatusArmedAway();
                                m_UbidotsStatus.sendStatus(newStatus);
                                receiveStatus();
                            }
                        }
                    });
                    builder.create();
                    builder.show();
                } else {
                    Status newStatus = new Status();
                    newStatus.setStatusDisarmed();
                    m_UbidotsStatus.sendStatus(newStatus);
                    receiveStatus();
                }
            }
        }
    };

    //When clicked, the local function for receiving the status is called. If a new status is received
    //the UI will redraw itself.
    private OnClickListener refreshButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            receiveStatus();
        }
    };
}
