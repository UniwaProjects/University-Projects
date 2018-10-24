#include "KeyManager.h"

KeyManager* KeyManager::instance = nullptr;

KeyManager* KeyManager::getInstance() {
	if (instance == nullptr) {
		instance = new KeyManager();
	}
	return instance;
}

KeyManager::KeyManager() {
	m_CurrentKey = keys_types::NoKey;
	resetPressedKeys();
	m_Keypad = new i2ckeypad(I2C_ADDRESS, ROWS, COLUMNS);
}

void KeyManager::reset() {
	m_CurrentKey = keys_types::NoKey;
	resetPressedKeys();
}

/*
Gets a new key and toggles the flag or flags
related with that key.
*/
void KeyManager::getNew() {
	m_CurrentKey = m_Keypad->get_key();
	resetPressedKeys();
	switch (m_CurrentKey) {
	case keys_types::NoKey:
		break;
	case keys_types::A:
		setKeyAPressed();
		break;
	case keys_types::B:
		setKeyBPressed();
		break;
	case keys_types::C:
		setKeyCPressed();
		break;
	case keys_types::D:
		setKeyDPressed();
		break;
	case keys_types::Hash:
		setHashKeyPressed();
		break;
	case keys_types::Star:
		setStarKeyPressed();
		break;
	default:
		setNumKeyPressed();
		break;
	}
}

/*
Gets the last pressed key in char type.
*/
char KeyManager::getCurrent() {
	return m_CurrentKey;
}

/*
The functions below return true or false if
the specific key related with that function
was pressed or no key was pressed.
*/
bool KeyManager::noKeyPressed() {
	if (m_CurrentKey == keys_types::NoKey) {
		return true;
	}
	else {
		return false;
	}
}

bool KeyManager::numberPressed() {
	return m_KeysPressed.number;
}

bool KeyManager::menuPressed() {
	return m_KeysPressed.menuKey;
}

bool KeyManager::enterPressed() {
	return m_KeysPressed.menuEnter;
}

bool KeyManager::nextPressed() {
	return m_KeysPressed.menuNext;
}

bool KeyManager::prevPressed() {
	return m_KeysPressed.menuPrev;
}

bool KeyManager::aPressed() {
	return m_KeysPressed.optionA;
}

bool KeyManager::bPressed() {
	return m_KeysPressed.optionB;
}

bool KeyManager::cPressed() {
	return m_KeysPressed.optionC;
}

bool KeyManager::dPressed() {
	return m_KeysPressed.optionD;
}

bool KeyManager::acceptPressed() {
	return m_KeysPressed.accept;
}

bool KeyManager::backspacePressed() {
	return m_KeysPressed.backspace;
}

/*
Resets all key function flags.
*/
void KeyManager::resetPressedKeys() {
	m_KeysPressed.number = false;
	m_KeysPressed.menuKey = false;
	m_KeysPressed.menuEnter = false;
	m_KeysPressed.menuNext = false;
	m_KeysPressed.menuPrev = false;
	m_KeysPressed.optionA = false;
	m_KeysPressed.optionB = false;
	m_KeysPressed.optionC = false;
	m_KeysPressed.optionD = false;
	m_KeysPressed.accept = false;
	m_KeysPressed.backspace = false;
}

/*
The functions below set all the function flags related
with the specific key to true.
*/
void KeyManager::setKeyAPressed() {
	m_KeysPressed.menuKey = true;
	m_KeysPressed.menuNext = true;
	m_KeysPressed.optionA = true;
}

void KeyManager::setKeyBPressed() {
	m_KeysPressed.menuPrev = true;
	m_KeysPressed.optionB = true;
}

void KeyManager::setKeyCPressed() {
	m_KeysPressed.menuEnter = true;
	m_KeysPressed.optionC = true;
}

void KeyManager::setKeyDPressed() {
	m_KeysPressed.optionD = true;
}

void KeyManager::setHashKeyPressed() {
	m_KeysPressed.accept = true;
}

void KeyManager::setStarKeyPressed() {
	m_KeysPressed.backspace = true;
}

void KeyManager::setNumKeyPressed() {
	m_KeysPressed.number = true;
}