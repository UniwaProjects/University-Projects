/*
	Inhrerits from the SerialManager class, shared between the Arduino and the ESP.
Includes the operations required for the Arduino part, handling sent and received
information via the Serial protocol.
*/
#pragma once

#include "Arduino.h"
#include "SerialManager.h"

class SpecializedSerial : public SerialManager {
public:
	SpecializedSerial(SpecializedSerial const&) = delete;
	void operator=(SpecializedSerial const&) = delete;
	static SpecializedSerial* getInstance();
	bool sendNetChange();
	bool sendReset();
	bool sendNetCredentials(const char* ssid, const char* pass);
	WifiInfo readNetInfo();
	bool readNetNotConnected();
	int8_t readNetworkHeader();
	WifiNetwork readNetwork();
	bool readNetworkEnd();

private:
	static SpecializedSerial* m_Instance;
	SpecializedSerial();
};