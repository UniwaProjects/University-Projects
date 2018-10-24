/*
	A serial manager whose purpose is to keep the copied buffer object, manipulate
it and reading the status from it, which will be a function shared across Arduino and
ESP8266.
*/
#pragma once

#include <Arduino.h>
#include "CharBuffer.h"
#include "GlobalTypes.h"

class SerialManager {
public:
	void begin(uint32_t baud_rate = 9600);
	bool getCommand();
	void clearSerial();
	bool sendStatus(Status currentStatus);
	Status readStatus(Status currentStatus);

protected:
	SerialManager();
	const uint8_t BUFFER_SIZE = 64; //Size of custom buffer, also max size of arduino serial buffer
	CharBuffer* m_SerialBuffer;
	bool getResponse(char* response, uint16_t wait);
};