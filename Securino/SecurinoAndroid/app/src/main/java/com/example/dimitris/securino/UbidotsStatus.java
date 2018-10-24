package com.example.dimitris.securino;

import android.support.annotation.NonNull;

import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

public class UbidotsStatus {
    //The physical device sends a signal every 5 mins, indicating a connection.
    //Checking the timeout every 6 minutes, giving a spare minute since repeating alarms
    //are not that accurate since API 19.
    public final int OFFLINE_MAX_MINS = 7;
    public boolean m_UISyncFlag; //Used from the UI to redraw only when a state is received
    //Used by the background service, to wait until the status is received, before sending a notification
    public boolean m_NotificationSyncFlag;

    //Ubidots constants, used to have access to the variables
    private final String API_KEY = "686102c3a2c6b75436891b138d1184d0aeaf5c2c";
    private final String STATE_ID = "5aaea8147625423216765f26";
    private final String METHOD_ID = "5aaea8147625423216765f28";
    private final String SENSOR_ID = "5aaea8147625423216765f27";
    private static UbidotsStatus m_StateManagerInstance = null; //Instance of the class
    private Status m_Status;
    private long m_Timestamp;
    //A serial excecutor, since the interaction with ubidots, changes both the local and online status
    //we want the changes to happen in a serial manner. For example if the status is changed by the user,
    //at the same time the service reads the online status, since both these tasks are asynchronous, there
    //is no way to know which one will finish first or if the state will be changed partially.
    private Executor m_SerialExecutor;

    private UbidotsStatus() {
        m_UISyncFlag = false;
        m_NotificationSyncFlag = false;
        //Each new runnable given on the executor, will be run in a separate thread.
        m_SerialExecutor = new SerialExecutor(new Executor() {
            @Override
            public void execute(@NonNull final Runnable command) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        command.run();
                    }
                }).start();
            }
        });
        m_Status = new Status();
        m_Timestamp = 0;
    }

    public static UbidotsStatus getInstance() {
        if (m_StateManagerInstance == null) {
            m_StateManagerInstance = new UbidotsStatus();
        }
        return m_StateManagerInstance;
    }

    public float getState() {
        return m_Status.getState();
    }

    public float getMethod() {
        return m_Status.getMethod();
    }

    public float getSensor() {
        return m_Status.getSensor();
    }

    public long getTimestamp() {
        return m_Timestamp;
    }

    //Adds the request to the executor queue, changes the status and then uploads it.
    public void sendStatus(final Status newStatus) {
        m_SerialExecutor.execute(new Runnable() {
            public void run() {
                android.util.Log.i("Info", "Send, thread id: " + Thread.currentThread().getId());
                try {
                    ApiClient apiClient = new ApiClient(API_KEY);
                    Variable stateVariable = apiClient.getVariable(STATE_ID);
                    Variable methodVariable = apiClient.getVariable(METHOD_ID);
                    Variable sensorVariable = apiClient.getVariable(SENSOR_ID);

                    stateVariable.saveValue(newStatus.getState());
                    methodVariable.saveValue(newStatus.getMethod());
                    sensorVariable.saveValue(newStatus.getSensor());
                } catch (Exception e) {
                    android.util.Log.i("Error", "Network Error");
                    e.printStackTrace();
                }
            }
        });
    }

    //Adds the request to the executor queue, downloads and sets the status depending on the online status.
    public void receiveStatus() {
        m_SerialExecutor.execute(new Runnable() {
            public void run() {
                android.util.Log.i("Info", "Receive, thread id: " + Thread.currentThread().getId());
                try {
                    ApiClient apiClient = new ApiClient(API_KEY);
                    //Set the alarm status
                    Variable stateVariable = apiClient.getVariable(STATE_ID);
                    Variable methodVariable = apiClient.getVariable(METHOD_ID);
                    Variable sensorVariable = apiClient.getVariable(SENSOR_ID);

                    Value[] stateValue = stateVariable.getValues();
                    Value[] methodValue = methodVariable.getValues();
                    Value[] sensorValue = sensorVariable.getValues();

                    if (String.valueOf(stateValue[0].getValue()).equals("0.0")) {
                        m_Status.setStatusDisarmed();
                    } else if (String.valueOf(stateValue[0].getValue()).equals("1.0")) {
                        if (String.valueOf(methodValue[0].getValue()).equals("1.0")) {
                            m_Status.setStatusArmedStay();
                        } else if (String.valueOf(methodValue[0].getValue()).equals("2.0")) {
                            m_Status.setStatusArmedAway();
                        }
                    } else if (String.valueOf(stateValue[0].getValue()).equals("2.0")) {
                        if (String.valueOf(sensorValue[0].getValue()).equals("0.0")) {
                            m_Status.setStatusAlertWrongPin();
                        } else if (String.valueOf(sensorValue[0].getValue()).equals("1.0")) {
                            m_Status.setStatusAlertMagnet();
                        } else if (String.valueOf(sensorValue[0].getValue()).equals("2.0")) {
                            m_Status.setStatusAlertPir();
                        } else if (String.valueOf(sensorValue[0].getValue()).equals("3.0")) {
                            m_Status.setStatusAlertSensorOffline();
                        }
                    }
                    //Timestamp
                    m_Timestamp = stateValue[0].getTimestamp();
                    m_UISyncFlag = true;
                    m_NotificationSyncFlag = true;
                } catch (Exception e) {
                    android.util.Log.i("Error", "Network Error");
                    e.printStackTrace();
                }
            }
        });
    }

    public class SerialExecutor implements Executor {
        final Queue<Runnable> m_Tasks = new ArrayDeque<>();
        final Executor m_Executor;
        Runnable m_Active;

        SerialExecutor(Executor executor) {
            this.m_Executor = executor;
        }

        public synchronized void execute(final Runnable runnable) {
            m_Tasks.add(new Runnable() {
                public void run() {
                    try {
                        runnable.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (m_Active == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((m_Active = m_Tasks.poll()) != null) {
                m_Executor.execute(m_Active);
            }
        }
    }
}