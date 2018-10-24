#pragma once

#include<inttypes.h>

/*
	The following structs and enums are essentials for the exchange of Wifi information
in the form of organized objects and not magic numbers.
*/
const uint8_t WIFI_MAX_LENGTH = 17; //16 characters for text and a '/0' character
typedef struct {
	char ssid[WIFI_MAX_LENGTH];
	char pass[WIFI_MAX_LENGTH];
} WifiCredentials;

const uint8_t IP_MAX_LENGTH = 16;//XXX.XXX.XXX.XXX, 12 numbers, 3 dots and a '/0' character
typedef struct {
	char ssid[WIFI_MAX_LENGTH];
	int32_t rssi;
	char localIp[IP_MAX_LENGTH];
} WifiInfo;

typedef enum t_WifiEncryption { 
	//Values map to 802.11 encryption suites
	Encryption_WEP = 5,
	Encrytpion_TKIP = 2,
	Encrytpion_CCMP = 4,
	//except these two, 7 and 8 are reserved in 802.11-2007
	Encrytpion_NONE = 7,
	Encrytpion_AUTO = 8
} t_WifiEncryption;

typedef struct {
	char ssid[WIFI_MAX_LENGTH];
	int32_t rssi;
	t_WifiEncryption encryption;
} WifiNetwork;

/*
	The following enums construct the final Status struct, which consists of the
alarm state, the arm method and the sensor state.
*/
typedef enum t_AlarmState {
	State_Disarmed = 0,
	State_Armed = 1,
	State_Alert = 2
} t_AlarmState;

typedef enum t_ArmMethod {
	Method_None = 0,
	Method_Arm_Stay = 1,
	Method_Arm_Away = 2
} t_ArmMethod;

typedef enum t_SensorTriggered {
	Sensor_None_Triggered = 0,
	Sensor_Pir_Triggered = 1,
	Sensor_Magnet_Triggered = 2,
	Sensor_Offline = 3
} t_SensorTriggered;

typedef struct {
	t_AlarmState state;
	t_ArmMethod method;
	t_SensorTriggered sensor;
} Status;