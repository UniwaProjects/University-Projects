/*
	This class is in charge of the custom i2c keypad and its input using the library
i2ckeypad. There is key mapping and there is a flag system with the assigned roles of
a button, so when for example "A" is pressed all the roles associated with A 
will be true. It also includes multiple boolean tests on the above flags in order
to simplify and hide the class' functionality and logical tests.
*/
#pragma once

#include <Arduino.h>
#include <i2ckeypad.h>

namespace keys_types {
	//Keymap of the keypad
	typedef enum KeyType {
		Hash = '#',
		Star = '*',
		A = 'A',
		B = 'B',
		C = 'C',
		D = 'D',
		NoKey = '\0'
	} KeyType;

	//Flags of assigned button roles
	struct KeysPressed {
		bool number : 1,
			accept : 1,
			backspace : 1,
			menuKey : 1,
			menuEnter : 1,
			menuNext : 1,
			menuPrev : 1,
			optionA : 1,
			optionB : 1,
			optionC : 1,
			optionD : 1;
	};
}

class KeyManager {
public:
	KeyManager(KeyManager const&) = delete;
	void operator=(KeyManager const&) = delete;
	static KeyManager* getInstance();
	void reset();
	void getNew();
	char getCurrent();
	bool noKeyPressed();
	bool numberPressed();
	bool menuPressed();
	bool enterPressed();
	bool nextPressed();
	bool prevPressed();
	bool aPressed();
	bool bPressed();
	bool cPressed();
	bool dPressed();
	bool acceptPressed();
	bool backspacePressed();

private:
	static KeyManager* instance;
	KeyManager();
	const uint8_t ROWS = 4; //Number of rows on the keypad
	const uint8_t COLUMNS = 4; //Number of columns on the keypad
	const uint8_t I2C_ADDRESS = 0x20; //I2C address of the PCF8574 chip
	i2ckeypad* m_Keypad;
	char m_CurrentKey;
	keys_types::KeysPressed m_KeysPressed;
	void resetPressedKeys();
	void setKeyAPressed();
	void setKeyBPressed();
	void setKeyCPressed();
	void setKeyDPressed();
	void setHashKeyPressed();
	void setStarKeyPressed();
	void setNumKeyPressed();
};