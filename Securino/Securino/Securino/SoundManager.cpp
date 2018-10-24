#include "SoundManager.h"

SoundManager* SoundManager::m_Instance = nullptr;

SoundManager* SoundManager::getInstance() {
	if (m_Instance == nullptr) {
		m_Instance = new SoundManager();
	}
	return m_Instance;
}

SoundManager::SoundManager() {}

/*
Uses the non blocking function tone to sound the G note.
This tone will be used for pin key presses.
*/
void SoundManager::pinKeyTone() {
	tone(BUZZER_PIN, NOTE_G, 250);
}

/*
Is used for menu key presses.
*/
void SoundManager::menuKeyTone() {
	tone(BUZZER_PIN, NOTE_A, 250);
}

/*
Tone used for correct actions. Delays are used
so that the tone will not get interrupted by another
key press.
*/
void SoundManager::successTone() {
	uint16_t durationMillis = 200;
	tone(BUZZER_PIN, NOTE_A, durationMillis);
	delay(durationMillis);
	tone(BUZZER_PIN, NOTE_G, durationMillis);
	delay(durationMillis);
}

/*
Same as the above function for oppossite actions.
*/
void SoundManager::failureTone() {
	uint16_t durationMillis = 400;
	tone(BUZZER_PIN, NOTE_A, durationMillis);
	delay(durationMillis);
}

/*
Used when the system goes on alarm. Since the tone function
without a duration will not stop on its own the noTone
function is needed in order for it to stop.
*/
void SoundManager::alarm() {
	tone(BUZZER_PIN, HIGH_FREQUENCY);
}

/*
Used to stop the tone function called from the alarm function.
*/
void SoundManager::stopAlarm() {
	noTone(BUZZER_PIN);
}
