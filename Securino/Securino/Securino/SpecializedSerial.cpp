#include "SpecializedSerial.h"

SpecializedSerial* SpecializedSerial::m_Instance = nullptr;

SpecializedSerial* SpecializedSerial::getInstance() {
	if (m_Instance == nullptr) {
		m_Instance = new SpecializedSerial();
	}
	return m_Instance;
}

SpecializedSerial::SpecializedSerial() {}

/*
Is used to request a wifi network change from the ESP.
The esp replies with an OK response and then a list of networks
to choose from. Returns true or false depending on the response.
*/
bool SpecializedSerial::sendNetChange() {
	Serial.println(F("CMD+CHANGE"));
	if (getResponse("RSP+OK", 500)) {
		return true;
	}
	return false;
}

/*
Requests a hardware reset on the ESP. An OK
response is expected.
*/
bool SpecializedSerial::sendReset() {
	Serial.println(F("CMD+RESET"));
	if (getResponse("RSP+OK", 500)) {
		return true;
	}
	return false;
}

/*
Sends wifi network credentials such as ssid and a password.
An ok response is expected and true is returned if received.
*/
bool SpecializedSerial::sendNetCredentials(const char* ssid, const char* pass) {
	Serial.print(F("CMD+CREDENTIALS:"));
	Serial.print(ssid);
	Serial.print(',');
	Serial.println(pass);
	if (getResponse("RSP+OK", 500)) {
		return true;
	}
	return false;
}

/*
Looks for a received command  in the form of "NET_INFO:SSID,RSSI,LOCALIP"
and if such a command with parameters within the allowed length is received,
a new WifiInfo obect is returned containing those. Otherwise an empty wifiinfo
object is returned.
*/
WifiInfo SpecializedSerial::readNetInfo() {
	WifiInfo nullInfo = { 0,0,0 };
	char* command = "INFO";
	if (m_SerialBuffer->find(command)) {
		WifiInfo newInfo = { 0,0,0 };
		// Skips the ":" after the command and gets the first char array
		//which is ssid.
		uint8_t arrayIndex = 0;
		uint8_t bufferIndex = strlen(command) + 1;
		do {
			newInfo.ssid[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			if (arrayIndex > WIFI_MAX_LENGTH) {
				Serial.println(F("RSP+BAD_SSID_LENGTH"));
				return nullInfo;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != ',');
		//Skips the ',' character and gets the second
		//char array which is rssi.
		arrayIndex = 0;
		bufferIndex++;
		char rssi[5] = { 0 };
		do {
			rssi[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			if (arrayIndex > 5) {
				Serial.println(F("RSP+BAD_RSSI"));
				return nullInfo;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != ',');
		//The buffer is then converted to int
		newInfo.rssi = atoi(rssi);

		//Skips the ',' character and gets the last
		//char array which is ip.
		arrayIndex = 0;
		bufferIndex++;
		do {
			newInfo.localIp[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			if (arrayIndex > IP_MAX_LENGTH) {
				Serial.println(F("RSP+BAD_IP_LENGTH"));
				return nullInfo;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != '\0');

		//Otherwise report OK and return the new info object
		Serial.println(F("RSP+OK"));
		return newInfo;
	}
	return nullInfo;
}

/*
Reads the buffer for a network disconnected command and returns
true if found, false otherwise. Then responds with a waiting
networks response.
*/
bool SpecializedSerial::readNetNotConnected() {
	char * command = "DISCONNECTED";
	if (m_SerialBuffer->find(command)) {
		Serial.println(F("RSP+OK"));
		return true;
	}
	return false;
}

/*
Reads the buffer for a network list header, which consists of
a networks start message, followed by the number of networks to come.
The returned number is the number of networks or -1 if too many networks
were received or the command wasn;t found.
The maximum number of networks allowed are 99.
*/
int8_t SpecializedSerial::readNetworkHeader() {
	char * command = "START_LIST";
	if (m_SerialBuffer->find(command)) {
		uint8_t arrayIndex = 0;
		uint8_t bufferIndex = strlen(command) + 1;
		//A 3 char array, 2 chars for the number and 1 for the terminator.
		char networks[3] = { 0 };
		do {
			networks[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			//If a bigger than a 2 digit number is received
			if (arrayIndex > 2) {
				Serial.println(F("RSP+TOO_MANY_NETWORKS"));
				return -1;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != '\0');
		//Otherwise report OK
		Serial.println(F("RSP+OK"));
		return atoi(networks);
	}
	return -1;
}

/*
Reads a wifi network from buffer in the form of "NETWORK:SSID,RSSI,ENCRYPTION".
If the parameters are within accepted lengths, a wifinetwork object filled
with the information received is returned and an OK response is sent via serial.
Otherwise an empty object is returned and a response with error information.
*/
WifiNetwork SpecializedSerial::readNetwork() {
	WifiNetwork nullNetwork = { 0,0,0 };
	char* command = "NETWORK";
	if (m_SerialBuffer->find(command)) {
		WifiNetwork network = { 0,0,0 };
		// Skips the ":" after the command and gets the first
		//char array which is ssid
		uint8_t arrayIndex = 0;
		uint8_t bufferIndex = strlen(command) + 1;
		do {
			network.ssid[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			if (arrayIndex > WIFI_MAX_LENGTH) {
				Serial.println(F("RSP+BAD_SSID_LENGTH"));
				return nullNetwork;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != ',');
		// Skips the "," character and gets the second
		//char array which is rssi
		arrayIndex = 0;
		bufferIndex++;
		char rssi[5] = { 0 };
		do {
			rssi[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			if (arrayIndex > 4) {
				Serial.println(F("RSP+BAD_RSSI"));
				return nullNetwork;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != ',');
		//Converte the rssi char array to int
		network.rssi = atoi(rssi);
		// Skips the "," character and gets the last
		//char array which is local ip
		arrayIndex = 0;
		bufferIndex++;
		network.encryption = (t_WifiEncryption)m_SerialBuffer->getInt(bufferIndex);
		//If the next character is not the termination character
		if (m_SerialBuffer->getChar(bufferIndex + 1) != '\0') {
			Serial.println(F("RSP+BAD_ENCRYPTION"));
			return nullNetwork;
		}

		//Otherwise report OK
		Serial.println(F("RSP+OK"));
		return network;
	}
	return nullNetwork;
}

/*
Reads the buffer for the networks end command and returns
true if found or false otherwise.
*/
bool SpecializedSerial::readNetworkEnd() {
	char * command = "END_LIST";
	if (m_SerialBuffer->find(command)) {
		Serial.println(F("RSP+OK"));
		return true;
	}
	return false;
}