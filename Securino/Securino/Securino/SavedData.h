/*
	Uses the EEPROM memory to save data. For the time it only saves the PIN code but
can be used to sort out EEPROM operations for better management of saved data locations
within the EEPROM in future created data than need to be saved.
*/
#pragma once

#include <Arduino.h>

namespace savedData {
	const uint8_t PIN_LENGTH = 5; //Length of pin
}

class SavedData {
public:
	SavedData(SavedData const&) = delete;
	void operator=(SavedData const&) = delete;
	static SavedData* getInstance();
	bool savePin(const char* pin);
	String readPin();

private:
	static SavedData* m_Instance;
	SavedData();
};