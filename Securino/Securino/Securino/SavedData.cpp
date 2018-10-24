#include "SavedData.h"
#include <EEPROM.h>

SavedData* SavedData::m_Instance = nullptr;

SavedData* SavedData::getInstance() {
	if (m_Instance == nullptr) {
		m_Instance = new SavedData();
	}
	return m_Instance;
}

SavedData::SavedData() {}

/*
Returns false if a pin larger than the allowed is given.
Else saves pin in EEPROM addresses 0 to 3 and then returns true.
*/
bool SavedData::savePin(const char* pin) {
	if (strlen(pin) > savedData::PIN_LENGTH) {
		return false;
	}
	else {
		for (uint8_t i = 0; i < savedData::PIN_LENGTH - 1; i++) {
			EEPROM.write(i, pin[i]);
		}
		return true;
	}
}

/*
Reads the first 4 addresses of the EEPROM and returns
a string of the pin.
*/
String SavedData::readPin() {
	char pinBuffer[savedData::PIN_LENGTH] = { 0 };
	for (uint8_t i = 0; i < savedData::PIN_LENGTH - 1; i++) {
		pinBuffer[i] = (char)EEPROM.read(i);
	}
	return String(pinBuffer);
}