/*
	A collection of user interface strings saved in Flash memory. The strings are read
from the flash memory using the inline FString function. This way aside from reducing
ram usage, it is made easy to translate the interface.
*/
#pragma once
#define ENGLISH

inline
__FlashStringHelper* FString(const char* text)
{
	return (__FlashStringHelper*)(text);
}

namespace text {
#ifdef ENGLISH
	const char VERSION[] PROGMEM = "Securino v3.21";
	const char ARMED[] PROGMEM = "Armed";
	const char MAGNET[] PROGMEM = "Magnet: ";
	const char PIR[] PROGMEM = "|Pir: ";
	const char DISARMED[] PROGMEM = "Disarmed";
	const char WIFI[] PROGMEM = "WiFi";
	const char MENU[] PROGMEM = "A: Menu";
	const char PIN[] PROGMEM = "D: PIN";
	const char STATE_CHANGE[] PROGMEM = "Changing State..";
	const char ALERT_TRIGGERED[] PROGMEM = "Alert Triggered";
	const char MENU_WIFI_INFO[] PROGMEM = "WiFi Information";
	const char MENU_CHANGE_WIFI[] PROGMEM = "Change  WiFi";
	const char MENU_CHANGE_PIN[] PROGMEM = "Change PIN";
	const char TESTING_SENSORS[] PROGMEM = "Testing Sensors";
	const char SETUP_SENSORS[] PROGMEM = "Setup Sensors";
	const char SENSOR[] PROGMEM = "Sensor ";
	const char ID[] PROGMEM = "ID ";
	const char NET[] PROGMEM = "Net";
	const char OK[] PROGMEM = " OK";
	const char SENSORS_OFFLINE[] PROGMEM = "Sensors Offline";
	const char BATTERY_LOW[] PROGMEM = "Battery Lo on ";
	const char MENU_LOAD_DEFAULTS[] PROGMEM = "Factory Defaults";
	const char MENU_RESET[] PROGMEM = "Reset";
	const char MENU_KEYS[] PROGMEM = "B  C: Enter  A";
	const char MENU_KEYS_LAST[] PROGMEM = "B  C: Enter";
	const char PROCEED_LINE_1[] PROGMEM = "Proceed?";
	const char PROCEED_LINE_2[] PROGMEM = "A: Yes B: No";
	const char DEFAULTS_LOADED[] PROGMEM = "Defaults  Loaded";
	const char ENTER_PIN[] PROGMEM = "PIN: ";
	const char NEW_PIN[] PROGMEM = "New PIN: ";
	const char PIN_CHANGED[] PROGMEM = "PIN  Changed";
	const char CORRECT[] PROGMEM = "Correct";
	const char INCORRECT[] PROGMEM = "Incorrect";
	const char TIMED_OUT[] PROGMEM = "Timed Out";
	//const char TRIES_LEFT[] PROGMEM = " Tries Left:  ";
	const char ARM_SELECT_LINE_1[] PROGMEM = "A. Arm Away";
	const char ARM_SELECT_LINE_2[] PROGMEM = "B. Arm Stay";
	const char ARM_DELAY_LINE_1[] PROGMEM = " Seconds Until";
	const char ARM_DELAY_LINE_2[] PROGMEM = "System is Armed";
	const char SSID[] PROGMEM = "SSID";
	const char WIFI_CONNECTING[] PROGMEM = "Connecting";
	const char WIFI_CONNECTED[] PROGMEM = "Connected!";
	const char WIFI_DISCONNECT[] PROGMEM = "Connect Failed";
	const char WIFI_CONNECT_KEYS[] PROGMEM = "B C: Connect A";
	const char WIFI_CONNECT_KEYS_SINGLE_NET[] PROGMEM = "C: Connect";
	const char WIFI_CONNECT_KEYS_FIRST[] PROGMEM = "   C: Connect A";
	const char WIFI_CONNECT_KEYS_LAST[] PROGMEM = "B C: Connect";
	const char NO_NETWORKS_LINE_1[] PROGMEM = "No WiFi Networks";
	const char NO_NETWORKS_LINE_2[] PROGMEM = "Available";
	const char ENCRYPTION[] PROGMEM = "Encryption: ";
	const char ENCR_NONE[] PROGMEM = "NONE";
	const char ENCR_WEP[] PROGMEM = "WEP";
	const char ENCR_WPA[] PROGMEM = "WPA";
	const char ENCR_WPA2[] PROGMEM = "WPA2";
	const char ENCR_AUTO[] PROGMEM = "AUTO";
	const char CHARACTER_LIMIT[] PROGMEM = "Up to 16 chrctrs";
	const char LOCAL_IP[] PROGMEM = "Local IP";
	const char WIFI_PASS_KEYS[] PROGMEM = "ABC #:Acc *:Del";
	const char WIFI_SCANNING[] PROGMEM = " Scanning . . .";
	const char PIN_KEYS[] PROGMEM = "Accept:# Del:*";
#endif
}