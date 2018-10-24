/*
	A simple countdown timer meant to return true with the timeout function if 
it is not reset in bewtween.
*/
#pragma once

#include "Arduino.h"

class Timer {
public:
	Timer(const uint16_t seconds);
	void reset();
	bool timeout();

private:
	uint16_t m_TimeoutSeconds;
	uint32_t m_Timer;
};