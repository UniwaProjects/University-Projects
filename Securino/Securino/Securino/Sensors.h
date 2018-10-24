/*
	Handles the RF24L01+ and RF24 libraries for the management
of the mesh network. Keeps track of all registered sensors and handles all
the communication.
*/
#pragma once

#include <Arduino.h>
#include "RF24Network.h"
#include "RF24.h"
#include "RF24Mesh.h"
#include <SPI.h>
//Include eeprom.h for AVR (Uno, Nano) etc. except ATTiny
#include <EEPROM.h>

namespace sensor_types {
	/*
	Sensor type represents the type of the sensor
	which are magnet and pir. None is used for empty
	type variable.
	*/
	typedef enum t_Sensor {
		Sensor_None = 0,
		Sensor_Magnet = 'M',
		Sensor_Pir = 'P'
	} t_Sensor;

	const uint8_t PING = 0;
	const uint8_t TRIGGERED = 1;
	const uint8_t LOW_BATTERY = 2;

	typedef struct SensorInfo {
		t_Sensor type = Sensor_None;
		int8_t id = 0;
		uint8_t state = PING;
		bool isNew = false;
	} SensorInfo;
	/*
	Sensors struct, each sensor has a type, an id and a lease
	in order to expire after the designated seconds pass and
	the sensor didn't ping
	*/
	typedef struct RadioSensor {
		t_Sensor type = Sensor_None;
		uint8_t id = 0;
		uint32_t lease = 0;
	} RadioSensor;
}

class Sensors {
public:
	Sensors(Sensors const&) = delete;
	void operator=(Sensors const&) = delete;
	static Sensors* getInstance();
	void begin();
	sensor_types::SensorInfo listen();
	bool isNetworkComplete();
	bool isSensorOffline();
	void reset();
	uint8_t getMagnetCount();
	uint8_t getPirCount();

private:
	static Sensors* m_Instance;
	Sensors();
	const uint8_t CHANNEL = 125; //Channel changes the communication frequency
	const uint8_t CE_PIN = 9; //Pin 9 of Arduino, used for control of RF24. 
	const uint8_t CSN_PIN = 10; //Pin 10 of Arduino, can only be output if using SPI. Chip select is an output.
	const uint8_t NODE_ID = 0; //Master node has an ID of 0
	RF24* m_Radio;
	RF24Network* m_Network;
	RF24Mesh* m_Mesh;

	const uint16_t LEASE_TIMEOUT = 30000; //Millis for sensor to communicate
	const uint8_t MAX_SENSORS = 10; //Max number of sensors in the network
	sensor_types::RadioSensor* m_Sensors;
	bool m_StartedListening;
	uint8_t m_PirCounter;
	uint8_t m_MagnetCounter;
	int8_t getArrayIndex(uint8_t sensorID);
	bool addToArray(uint8_t sensorType, uint8_t sensorID);
};