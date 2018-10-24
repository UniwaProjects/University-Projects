package com.example.dimitris.securino;

/*
Includes all possible states and provides a controlled way of setting the status, without
spreading magic numbers throughout the code or providing out of bounds values to Ubidots.
*/
public class Status {
    //Public constants of the possible states
    public static final float STATE_DISARMED = 0.0f;
    public static final float STATE_ARMED = 1.0f;
    public static final float STATE_ALERT = 2.0f;
    public static final float METHOD_NO_ARM = 0.0f;
    public static final float METHOD_ARM_STAY = 1.0f;
    public static final float METHOD_ARM_AWAY = 2.0f;
    public static final float SENSOR_NOT_TRIGGERED = 0.0f;
    public static final float SENSOR_MAGNET_TRIGGERED = 1.0f;
    public static final float SENSOR_PIR_TRIGGERED = 2.0f;
    public static final float SENSOR_OFFLINE = 3.0f;

    private float m_State;
    private float m_Method;
    private float m_Sensor;

    public Status() {
        this.m_State = STATE_DISARMED;
        this.m_Method = METHOD_NO_ARM;
        this.m_Sensor = SENSOR_NOT_TRIGGERED;
    }

    public float getState() {
        return m_State;
    }

    public float getMethod() {
        return m_Method;
    }

    public float getSensor() {
        return m_Sensor;
    }

    public void setStatusDisarmed() {
        this.m_State = STATE_DISARMED;
        this.m_Method = METHOD_NO_ARM;
        this.m_Sensor = SENSOR_NOT_TRIGGERED;
    }

    public void setStatusArmedStay() {
        this.m_State = STATE_ARMED;
        this.m_Method = METHOD_ARM_STAY;
        this.m_Sensor = SENSOR_NOT_TRIGGERED;
    }

    public void setStatusArmedAway() {
        this.m_State = STATE_ARMED;
        this.m_Method = METHOD_ARM_AWAY;
        this.m_Sensor = SENSOR_NOT_TRIGGERED;
    }

    public void setStatusAlertWrongPin() {
        this.m_State = STATE_ALERT;
    }

    public void setStatusAlertMagnet() {
        this.m_State = STATE_ALERT;
        this.m_Sensor = SENSOR_MAGNET_TRIGGERED;
    }

    public void setStatusAlertPir() {
        this.m_State = STATE_ALERT;
        this.m_Sensor = SENSOR_PIR_TRIGGERED;
    }

    public void setStatusAlertSensorOffline() {
        this.m_State = STATE_ALERT;
        this.m_Sensor = SENSOR_OFFLINE;
    }
}
