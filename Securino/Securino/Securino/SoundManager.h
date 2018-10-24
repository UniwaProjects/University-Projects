/*
	This class is responsible for audio feedback using the buzzer. There are
different notes for different types of action, in order of actions to be auditably
recognizable.
*/
#pragma once

#include <Arduino.h>

class SoundManager {
public:
	SoundManager(SoundManager const&) = delete;
	void operator=(SoundManager const&) = delete;
	static SoundManager* getInstance();
	void pinKeyTone();
	void menuKeyTone();
	void successTone();
	void failureTone();
	void alarm();
	void stopAlarm();

private:
	static SoundManager* m_Instance;
	SoundManager();
	const uint8_t BUZZER_PIN = 8; //Arduino pin of buzzer
	const uint16_t NOTE_G = 392; //G note's frequency
	const uint16_t NOTE_A = 440; //A note's frequency
	const uint16_t HIGH_FREQUENCY = 1500; //A high pitched frequency
};