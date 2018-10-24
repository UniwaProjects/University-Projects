/*
	Complementing the SerialManager for this device's purpose, it handles additional
commands exchange fitting to this device's needs.
*/
#pragma once

#include <Arduino.h>
#include "SerialManager.h"
#include "GlobalTypes.h"

class SpecializedSerial : public SerialManager {
public:
	SpecializedSerial(SpecializedSerial const&) = delete;
	void operator=(SpecializedSerial const&) = delete;
	static SpecializedSerial* getInstance();
	bool sendNetInfo(const String& ssid, int32_t rssi, const String& ip);
	bool sendNetNotConnected();
	bool sendNetsStart(uint8_t networksCount);
	bool sendNetsList(const String& ssid, uint8_t encryption, int32_t rssi);
	bool sendNetsEnd();
	bool readNetChange();
	bool readReset();
	WifiCredentials readCredentials();

private:
	static SpecializedSerial* instance;
	SpecializedSerial();
};