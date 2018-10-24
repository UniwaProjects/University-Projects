/*
Note: The yield() function is used through out loops that extend to an
unknown amount of seconds. If the loop extends to more than 50ms, calling
a delay or yield() (which is equal to delay(0);) is recommended as it keeps
the WiFi stack running smoothly. TCP/IP and WiFi operations are handled only
at the end of each loop() and during delays.
*/
#include <ESP8266WiFi.h>
#include "GlobalTypes.h"
#include "Timer.h"
#include "SpecializedSerial.h"
#include "UbidotsManager.h"
#include "SavedData.h"

/*******************
CLASS OBJECTS
*******************/
SpecializedSerial* g_Serial = SpecializedSerial::getInstance();
UbidotsManager* g_UbidotsClient = UbidotsManager::getInstance();
SavedData* g_SavedData = SavedData::getInstance();

Status g_Status = { State_Disarmed, Method_None, Sensor_None_Triggered };
WifiCredentials g_NetCredentials = { 0, 0 };

const uint16_t UPDATE_INTERVAL_SECS = 300;
const uint16_t DOWNLOAD_INTERVAL_SECS = 30;
Timer g_UpdateVariablesTimer = Timer(UPDATE_INTERVAL_SECS); //Timer to update ubidots variables
Timer g_GetVariablesTimer = Timer(DOWNLOAD_INTERVAL_SECS); //Timer to check for online variable changes

void getNetCredentials();
bool connectToNet();
uint8_t sendScannedNets();
void sendNotConnectedToNet();
void checkLocalStateChange();
void checkNetChange();
void checkReset();
void checkOnlineStateChange();
void timedStateUpdate();

void setup() {
	WiFi.mode(WIFI_STA); //Set ESP8266 to client mode
	g_Serial->begin();
	g_SavedData->begin();
	//Copy credentials from EEPROM
	g_NetCredentials = g_SavedData->readWifiCredentials();
	//Connect to wifi
	bool connected = connectToNet();
	//If a connection cannot be established, probably because the
	//ssid was not found, a new list of networks is sent.
	//New ssid and password is expected from user.
	while (!connected) {
		sendNotConnectedToNet();
		connected = connectToNet();
		yield();
	}
	//Send the state to ubidots
	g_UbidotsClient->sendStatus(g_Status);
}

/*
  If there is an established wifi connection then the ESP8266 checks
  for changes(made from the mobile app) on the ubidots platform,
  for changes on the state of the arduino and lastly it perfoms an
  update every 5 minutes as an indicator that the alarm is still powered on
  and online.
*/

void loop() {
	//Checks if there is a received serial command,
	//gets the command to the custom serial buffer
	//and then compares it with possible commands.
	//Then clears the buffer.
	if (g_Serial->getCommand()) {
		checkLocalStateChange();
		checkNetChange();
		checkReset();
		g_Serial->clearSerial();
	}

	//If Wifi is connected, every few seconds the program
	//checks for an online state change and if such a change
	//has occured, the change is sent to the arduino.
	//Also every few seconds the online variables are updated to indicate
	//that the alarm is online and working.
	if (WiFi.isConnected()) {
		checkOnlineStateChange();
		timedStateUpdate();
	}
	//Else a troubleshooting takes place depending on
	//the state of the alarm. If the user is home the alarm prompts
	//him to reconnect to an active network. Otherwise it constantly
	//tries a reconnect to the previous network.
	else {
		if (g_Status.state == State_Disarmed
			|| g_Status.method == Method_Arm_Stay) {
			sendNotConnectedToNet();
		}
		connectToNet();
	}
}

/*
  Reads the command and if its not a credentials command, it tries until credentials
  are given. If it is a credentials command, if valid credentials were given,
  it replaces the current credentials.
*/
void getNetCredentials() {
	bool done = false;
	while (!done) {
		if (g_Serial->getCommand()) {
			WifiCredentials newCredentials = { 0,0 };
			newCredentials = g_Serial->readCredentials();
			if (strlen(newCredentials.ssid) > 0) {
				g_NetCredentials = newCredentials;
				done = true;
			}
			g_Serial->clearSerial();
		}
		yield();
	}
	g_SavedData->saveWifiCredentials(g_NetCredentials);
}

/*
  Attempts to establish a wifi connection with the global credentials. If a
  connection is established, the network info (SSID, RSSI, Local IP), are sent to
  the arduino.
*/

bool connectToNet() {
	WiFi.disconnect(); //Disconnects from the connected network
	delay(500); //Delay ensures disconnection happens before trying to reconnect
	//Use the credentials to establish a connection via ubidots
	g_UbidotsClient->wifiConnect(g_NetCredentials.ssid, g_NetCredentials.pass);
	//Frequent delays are needed for the wifi libraries, without a delay here
	//the WiFi library won't have time to refresh its contents and wifi will
	//report connected. This happens because it's been some time from the last 
	//end of loop at which point those libraries do any pending operations.
	delay(500);
	//If connected send network info to arduino
	if (WiFi.isConnected()) {
		bool sent = false;
		while (!sent) {
			sent = g_Serial->sendNetInfo(
				WiFi.ESP8266WiFiSTAClass::SSID(),
				WiFi.ESP8266WiFiSTAClass::RSSI(),
				WiFi.ESP8266WiFiSTAClass::localIP().toString());
			yield();
		}
		return true;
	}
	return false;
}

/*
Scans networks and if the returned count is equal to 0 or better, a list
is sent back to the arduino. -1 is defined as "scan running" and -2 as "scan failed".
*/
uint8_t sendScannedNets() {
	int8_t networksCount = WiFi.scanNetworks();
	if (networksCount >= 0) {
		while (!g_Serial->sendNetsStart(networksCount)); {
			yield();
		}
		if (networksCount > 0) {
			for (uint8_t i = 0; i < networksCount; i++) {
				bool sent = false;
				while (!sent) {
					sent = g_Serial->sendNetsList(
						WiFi.ESP8266WiFiScanClass::SSID(i),
						WiFi.ESP8266WiFiScanClass::encryptionType(i),
						WiFi.ESP8266WiFiScanClass::RSSI(i));
					yield();
				}
				yield();
			}
			while (!g_Serial->sendNetsEnd()) {
				yield();
			}
		}
	}
	return networksCount;
}

void sendNotConnectedToNet() {
	if (g_Serial->sendNetNotConnected()) {
		if (sendScannedNets() > 0) {
			getNetCredentials();
		}
	}
}

/*
  Reads the state command from serial and compares it to the current state.
  If the current state is the same with the new one, then nothing happens.
  Otherwise the new state overides the old and ubidots variables are updated.
*/
void checkLocalStateChange() {
	Status newStatus = g_Serial->readStatus(g_Status);
	if ((g_Status.state != newStatus.state)
		|| (g_Status.method != newStatus.method)
		|| (g_Status.sensor != newStatus.sensor)) {

		g_Status = newStatus;
		g_UbidotsClient->sendStatus(g_Status);
	}
}

/*
If a network change is requested, a scan of the networks is sent,
credntials are received and a connection to the network is attempted.
Whether the it connects to the network or not as well as the reattempts
are handled inside the main loop().
*/
void checkNetChange() {
	if (g_Serial->readNetChange()) {
		g_Serial->clearSerial();
		if (sendScannedNets() > 0) {
			getNetCredentials();
			connectToNet();
		}
	}
}

/*
It restarts the ESP upon request from the arduino.
*/
void checkReset() {
	if (g_Serial->readReset()) {
		//ESP.reset();
		ESP.restart();
	}
}

/*
  This function is responsible for reading the ubidots variables every 30 seconds
  and then compare them to the existing state. If the variables are different from the
  stored ones then that means that a user changed the state from the mobile app.
  The function then stores the variables, replacing the excisting ones and
  sends a state change command to the arduino.
*/

void checkOnlineStateChange() {
	//If 30 seconds have passed
	if (g_GetVariablesTimer.timeout()) {
		//Storing the existing state
		Status newStatus = g_UbidotsClient->getStatus(g_Status);
		//If the previous state isnot the same with the currently read,
		//then replace the current state and send a serial command 
		//to the arduino for the state to change.
		//Last, reset the timer.
		if ((g_Status.state != newStatus.state)
			|| (g_Status.method != newStatus.method)
			|| (g_Status.sensor != newStatus.sensor)) {
			g_Status = newStatus;
			g_Serial->sendStatus(g_Status);
		}
		g_GetVariablesTimer.reset();
	}
}

/*
  Every 5 minutes updates the online variables, indicating that the system is
  on and online. It then resets the timer.
*/
void timedStateUpdate() {
	if (g_UpdateVariablesTimer.timeout()) {
		g_UbidotsClient->sendStatus(g_Status);
		g_UpdateVariablesTimer.reset();
	}
}