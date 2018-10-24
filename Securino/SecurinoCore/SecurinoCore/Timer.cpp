#include "Timer.h"

Timer::Timer(const uint16_t seconds) {
	m_TimeoutSeconds = seconds;
	m_Timer = millis();
}

/*
Sets the timer to current milliseconds using
a function from Arduino.h, esentially resetting it.
*/
void Timer::reset() {
	m_Timer = millis();
}

/*
Returns true if the current milliseconds minus the last
millisecond entry are larger than the timeout(multiplied by 1000
to convert them to seconds).
*/
bool Timer::timeout() {
	if (millis() - m_Timer > (m_TimeoutSeconds * 1000)) {
		return true;
	}
	else {
		return false;
	}
}