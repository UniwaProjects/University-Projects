#include "SerialManager.h"

SerialManager::SerialManager() {
	m_SerialBuffer = new CharBuffer(BUFFER_SIZE);
}

/*
Default baud rate is 9600, since its a moderate
speed for small amount of data with small error
rate.
*/
void SerialManager::begin(uint32_t baud_rate) {
	Serial.begin(baud_rate);
}

/*
If a serial connection is available it reads the entire Serial Buffer
searching for a command, copies it into our own serial buffer and then
return true. Otherwise if there is no command to read it returns false.
The purpose of this transfer is that serial data are preserved and can be read
from multiple functions without losing the data upon reading them as it
happens with the original serial buffer.
*/
bool SerialManager::getCommand() {
	//If there are bytes to read
	if (Serial.available()) {
		//If there is a command on buffer
		if (Serial.find("CMD+")) {
			delay(100); //WITHOUT THIS DELAY EVERYTHING FALLS APART
			m_SerialBuffer->clear();
			//Read all the bytes up to /n (/r/n is the default serial
			//end of lines), avoiding copying those special characters
			uint8_t i = 0;
			char c = 0;
			do {
				c = (char)Serial.read();
				if (c != '\n' && c != '\r') {
					m_SerialBuffer->setChar(i, c);
					i++;
				}
			} while (c != '\n');
			return true;
		}
	}
	return false;
}

/*
Reads leftover characters and clears the custom serial buffer
so that the find() of the next commands wont have to search
through trash or read an earlier response.
*/
void SerialManager::clearSerial() {
	m_SerialBuffer->clear();
	while (Serial.read() != -1)
		;
}

/*
Sends the state via Serial to the ESP. If the ESP replies with
an OK response it returns true, otherwise false.
*/
bool SerialManager::sendStatus(Status currentStatus) {
	Serial.print(F("CMD+STATUS:"));
	Serial.print(currentStatus.state);
	Serial.print(F(","));
	Serial.print(currentStatus.method);
	Serial.print(F(","));
	Serial.println(currentStatus.sensor);
	return getResponse("RSP+OK", 500);
}

/*
Searches the buffer for the "STATE" command. If the command is found
and the values are within the desired range it returns the new state. In any
different case (couldn't fetch the command, bad values), it returns the
previous state.
*/
Status SerialManager::readStatus(Status currentStatus) {
	char* command = "STATUS";
	if (m_SerialBuffer->find(command)) {
		Status newStatus = { State_Disarmed, Method_None, Sensor_None_Triggered };
		//Skips the ":" after the command and gets the first number
		//which is state
		uint8_t arrayIndex = strlen(command) + 1;
		uint8_t state = m_SerialBuffer->getInt(arrayIndex);
		//Skip the ',' and read the next number which is arm method
		arrayIndex = arrayIndex + 2;
		uint8_t arm = m_SerialBuffer->getInt(arrayIndex);
		//Skip the ',' and read the last number which is sensor state
		arrayIndex = arrayIndex + 2;
		uint8_t sensor = m_SerialBuffer->getInt(arrayIndex);
		//If everything is within limits
		if ((state >= 0 && state <= 2) && (arm >= 0 && arm <= 2)
			&& (sensor >= 0 && sensor <= 3)) {
			newStatus.state = (t_AlarmState)state;
			newStatus.method = (t_ArmMethod)arm;
			newStatus.sensor = (t_SensorTriggered)sensor;
			Serial.println(F("RSP+OK"));
			return newStatus;
		}
		else {
			Serial.println(F("RSP+BAD_VALUE"));
		}
	}
	return currentStatus;
}

/*
Waits the desired amount of time and then reads the response returning
true if found or false otherwise
*/
bool SerialManager::getResponse(char* response, uint16_t wait) {
	if (wait > 0) {
		delay(wait);
	}
	return Serial.find(response);
}