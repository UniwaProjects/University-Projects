#include "SavedData.h"

SavedData* SavedData::instance = nullptr;

SavedData* SavedData::getInstance() {
	if (instance == nullptr) {
		instance = new SavedData();
	}
	return instance;
}

SavedData::SavedData() {}

/*
Note: ESP8266 contains no EEPROM, it emulates the EEPROM through the flash memory.
That means that EEPROM memory must be declared as EEPROM.begin(SIZE); and an
additional EEPROM.commit() must be called after calls to the write funtion.
*/
void SavedData::begin() {
	EEPROM.begin(EEPROM_MEMORY);
}

/*
Replaces the writen part of the memory with zeroes, essentially deleting it.
*/
void SavedData::eraseMemory() {
	for (uint8_t i = 0; i < EEPROM_MEMORY; i++) {
		EEPROM.write(i, 0);
	}
	EEPROM.commit();
}

/*
SSID extends from the first byte of EEPROM memory to the 17th(0 through 16).
The first byte is the actual word length of the ssid and a loop of that many
iterations takes place to read its characters. Then a string is returned.
The reason behind SSID and the length byte taking 17 bytes of space instead of
18 (17 for SSID and 1 for length) is that the null terminator is not stored.
*/
String SavedData::readWifiSSID() {
	char ssidBuffer[WIFI_MAX_LENGTH] = { 0 };
	uint8_t eepromIndex = 0;
	uint8_t ssidLength = (uint8_t)EEPROM.read(eepromIndex);
	eepromIndex++;
	for (uint8_t i = 0; i < ssidLength; i++) {
		ssidBuffer[i] = (char)EEPROM.read(eepromIndex + i);
	}
	return String(ssidBuffer);
}

/*
Password extends from the 18th byte of EEPROM memory to the 35th(17 through 34).
The first byte is the actual word length of the password and a loop of that many
iterations takes place to read its characters. Then a string is returned.
*/
String SavedData::readWifiPass() {
	char passBuffer[WIFI_MAX_LENGTH] = { 0 };
	uint8_t eepromIndex = WIFI_MAX_LENGTH;
	uint8_t passLength = (uint8_t)EEPROM.read(eepromIndex);
	eepromIndex++;
	for (uint8_t i = 0; i < passLength; i++) {
		passBuffer[i] = (char)EEPROM.read(eepromIndex + i);
	}
	return String(passBuffer);
}

/*
A way to save the WifiCredentials object deirectly. Also since credentials
are a pair it makes more sense to be stored as a pair and not store one or another.
If the length of the given credentials is within range the credentials are saved.
*/
bool SavedData::saveWifiCredentials(WifiCredentials credentials) {
	if (strlen(credentials.ssid) < WIFI_MAX_LENGTH
		&& strlen(credentials.pass) < WIFI_MAX_LENGTH) {
		saveWifiSSID(credentials.ssid);
		saveWifiPass(credentials.pass);
		return true;
	}
	return false;
}

/*
A way to read to a WifiCredentials object directly. Also since credentials
are a pair it makes more sense to be read as a pair and not read one or another.
*/
WifiCredentials SavedData::readWifiCredentials() {
	WifiCredentials credentials = { 0,0 };
	strncpy(credentials.ssid, readWifiSSID().c_str(), WIFI_MAX_LENGTH);
	strncpy(credentials.pass, readWifiPass().c_str(), WIFI_MAX_LENGTH);
	return credentials;
}

/*
Writes the length of the ssid as the first bytein EEPROM prosition 0, following
with the ssid. Calls commit after writing for the reasons stated in the note.
*/
void SavedData::saveWifiSSID(const char* ssid) {
	uint8_t ssidLength = strlen(ssid);
	uint8_t eepromIndex = 0;
	EEPROM.write(eepromIndex, ssidLength);
	eepromIndex++;
	for (uint8_t i = 0; i < ssidLength; i++) {
		EEPROM.write(eepromIndex + i, ssid[i]);
	}
	EEPROM.commit();
}
/*
Writes the length of the pass as the first bytein EEPROM prosition WIFI_MAX_LENGTH,
following with the pass.
Calls commit after writing for the reasons stated in the note.
*/
void SavedData::saveWifiPass(const char* pass) {
	uint8_t passLength = strlen(pass);
	uint8_t eepromIndex = WIFI_MAX_LENGTH;
	EEPROM.write(eepromIndex, passLength);
	eepromIndex++;
	for (uint8_t i = 0; i < passLength; i++) {
		EEPROM.write(eepromIndex + i, pass[i]);
	}
	EEPROM.commit();
}