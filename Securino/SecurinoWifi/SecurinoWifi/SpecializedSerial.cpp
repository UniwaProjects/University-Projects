#include "SpecializedSerial.h"

SpecializedSerial* SpecializedSerial::instance = nullptr;

SpecializedSerial* SpecializedSerial::getInstance() {
	if (instance == nullptr) {
		instance = new SpecializedSerial();
	}
	return instance;
}

SpecializedSerial::SpecializedSerial() {}

/*
Sends network info via serial and expects to read an RSP+OK response. Returns true
if response received within time limit or false otherwise.
*/
bool SpecializedSerial::sendNetInfo(const String& ssid, int32_t rssi, const String& ip) {
	Serial.print(F("CMD+INFO:"));
	Serial.print(ssid);
	Serial.print(F(","));
	Serial.print(rssi);
	Serial.print(F(","));
	Serial.println(ip);
	return getResponse("RSP+OK", 500);
}

/*
Sends a network disconnected message via serial and expects to read
an RSP+WAITING_NETWORKS response. Returns true if response received
within time limit or false otherwise.
*/
bool SpecializedSerial::sendNetNotConnected() {
	Serial.println(F("CMD+DISCONNECTED"));
	return getResponse("RSP+OK", 500);

}

/*
Sends a network list after sending a start message with the ammount of networks
that will be sent and followed by networks and an end message. All 3 functions
return true if responses to the corrensponding message were received.
*/
bool SpecializedSerial::sendNetsStart(uint8_t networksCount) {
	Serial.print(F("CMD+START_LIST:"));
	Serial.println(networksCount);
	return getResponse("RSP+OK", 1000);
}

bool SpecializedSerial::sendNetsList(const String& ssid, uint8_t encryption, int32_t rssi) {
	Serial.print(F("CMD+NETWORK:"));
	Serial.print(ssid);//max 17(will check)
	Serial.print(',');//1
	Serial.print(rssi);//max 4
	Serial.print(',');//1
	Serial.println(encryption); //1
	return getResponse("RSP+OK", 1000);
}

bool SpecializedSerial::sendNetsEnd() {
	Serial.println(F("CMD+END_LIST"));
	return getResponse("RSP+OK", 1000);
}

/*
Returns true if the command in the custom serial buffer is the net change command.
*/
bool SpecializedSerial::readNetChange() {
	if (m_SerialBuffer->find("CHANGE")) {
		Serial.println(F("RSP+OK"));
		return true;
	}
	return false;
}

/*
Returns true if the command in the custom serial buffer is the reset command.
*/
bool SpecializedSerial::readReset() {
	if (m_SerialBuffer->find("RESET")) {
		Serial.println(F("RSP+OK"));
		return true;
	}
	return false;
}
/*
  Searches the buffer for the "CREDENTIALS" command. If the command is found
  and the values are within the desired range it returns the new wifi credentials.
  In any different case (couldn't fetch the command, bad values), it returns a null
  credentials object.
*/
WifiCredentials SpecializedSerial::readCredentials() {
	char * command = "CREDENTIALS";
	WifiCredentials nullCredentials = { 0, 0 };
	if (m_SerialBuffer->find(command)) {
		WifiCredentials credentials = { 0, 0 };
		//m_SerialBuffer is read begining after the command and the ':' character
		uint8_t arrayIndex = 0;
		uint8_t bufferIndex = strlen(command) + 1;
		//Copy characters until the break ',' character is read
		do {
			credentials.ssid[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			if (arrayIndex > WIFI_MAX_LENGTH) {
				Serial.println(F("RSP+BAD_SSID_LENGTH"));
				return nullCredentials;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != ',');

		arrayIndex = 0;
		bufferIndex++; //Skip the ',' character
		//Copy characters until the last character
		do {
			credentials.pass[arrayIndex] = m_SerialBuffer->getChar(bufferIndex);
			arrayIndex++;
			if (arrayIndex > WIFI_MAX_LENGTH) {
				Serial.println(F("RSP+BAD_PASSWORD_LENGTH"));
				return nullCredentials;
			}
			bufferIndex++;
		} while (m_SerialBuffer->getChar(bufferIndex) != '\0');
		//Otherwise report OK
		Serial.println(F("RSP+OK"));
		return credentials;
	}
	return nullCredentials;
}
