/*
	Saves SSID and Password of the connected WiFi network to the EEPROM memory or
to be more precise, to the emulated EEPROM memory. ESP8266 has no EEPROM so part of
the flash memory is used to emulate an EEPROM.
*/
#pragma once

#include <Arduino.h>
#include <EEPROM.h>
#include "GlobalTypes.h"

class SavedData {
  public:
    SavedData(SavedData const&) = delete;
    void operator=(SavedData const&) = delete;
    static SavedData* getInstance();
	void begin();
	void eraseMemory();
    bool saveWifiCredentials(WifiCredentials credentials);
	WifiCredentials readWifiCredentials();

  private:
    static SavedData* instance;
    SavedData();
	const uint8_t EEPROM_MEMORY = WIFI_MAX_LENGTH * 2;
	void saveWifiSSID(const char* ssid);
	void saveWifiPass(const char* pass);
	String readWifiSSID();
	String readWifiPass();
};