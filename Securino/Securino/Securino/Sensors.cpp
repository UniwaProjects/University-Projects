#include "Sensors.h"
//#define DEBUG

Sensors* Sensors::m_Instance = nullptr;

Sensors* Sensors::getInstance() {
	if (m_Instance == nullptr) {
		m_Instance = new Sensors();
	}
	return m_Instance;
}

Sensors::Sensors() {
	m_Radio = new RF24(CE_PIN, CSN_PIN);
	m_Network = new RF24Network(*m_Radio);
	m_Mesh = new RF24Mesh(*m_Radio, *m_Network);
	m_Sensors = (sensor_types::RadioSensor *)malloc(MAX_SENSORS * sizeof(sensor_types::RadioSensor));
}

/*
Initialiazes the RF24 radio mesh network on the requested channel and sets 
the power amplifier to low for stable communication. It then initializes lists
and variables with the reset function.
*/
void Sensors::begin() {
	m_Mesh->setNodeID(NODE_ID);
	m_Mesh->begin(CHANNEL, RF24_1MBPS);
	m_Radio->setPALevel(RF24_PA_LOW);
	reset();
}

/*
With calls to update() and DHCP() the mesh is created and sustained. It then 
listens for sensor messages, registers new sensors and rewnews their lease. It then
returns the current sensor object.
*/
sensor_types::SensorInfo Sensors::listen() {
	if (m_StartedListening) {
		m_Radio->flush_rx();
		m_StartedListening = false;
	}
	m_Mesh->update();
	m_Mesh->DHCP();
	sensor_types::SensorInfo sensor;
	if (m_Network->available()) {

		RF24NetworkHeader header;
		m_Network->peek(header);
		m_Network->read(header, &sensor.state, sizeof(sensor.state));
		sensor.type = (sensor_types::t_Sensor)header.type;
		sensor.id = m_Mesh->getNodeID(header.from_node);
#ifdef DEBUG
		Serial.print(F("[Radio] R:"));
		Serial.print(sensor.type);
		Serial.print(sensor.id);
		Serial.println(sensor.state);

		Serial.println(F("********Assigned Addresses********"));
		for (int i = 0; i < m_Mesh->addrListTop; i++) {
			Serial.print("NodeID: ");
			Serial.print(m_Mesh->addrList[i].nodeID);
			Serial.print(" RF24Network Address: 0");
			Serial.println(m_Mesh->addrList[i].address, OCT);
		}
		Serial.println(F("**********************************"));
#endif // DEBUG
		if (sensor.type == sensor_types::Sensor_Magnet || sensor.type == sensor_types::Sensor_Pir) {
			if (sensor.id > 0) {
				int8_t sensorIndex = getArrayIndex(sensor.id);
				if (sensorIndex < 0) {
					sensor.isNew = addToArray(header.type, sensor.id);
				}
			}
		}
	}
	return sensor;
}

/*
Searches for the specified sensor id. If the id is found, the lease gets renewed
and the id is returned. Otherwise -1 is returned to indicate failure.
*/
int8_t Sensors::getArrayIndex(uint8_t sensorID) {
	for (uint8_t i = 0; i < MAX_SENSORS; i++) {
		if ((m_Sensors + i)->id == sensorID) {
			(m_Sensors + i)->lease = millis();
			return i;
		}
	}
	return -1;
}

/*
Returns true if an empty position is found in the sensor list
and the sensor is added, or false otherwise.
*/
bool Sensors::addToArray(uint8_t sensorType, uint8_t sensorID) {
	//If the list isn't full
	if ((m_MagnetCounter + m_PirCounter) < MAX_SENSORS) {
		int8_t emptyIndex = -1;
		for (int8_t i = 0; i < MAX_SENSORS; i++) {
			if ((m_Sensors + i)->type == sensor_types::Sensor_None) {
				emptyIndex = i;
				break;
			}
		}

		if (emptyIndex >= 0) {
			(m_Sensors + emptyIndex)->type = (sensor_types::t_Sensor)sensorType;
			(m_Sensors + emptyIndex)->id = sensorID;
			(m_Sensors + emptyIndex)->lease = millis();

			switch (sensorType) {
			case sensor_types::Sensor_Magnet:
				m_MagnetCounter++;
				break;
			case sensor_types::Sensor_Pir:
				m_PirCounter++;
				break;
			}
			return true;
		}
	}
	return false;
}

/*
Compares the mesh DHCP to the internal sensor list. If the index of the DHCP registered
sensors is not found in the internal sensor list then false is returned. True otherwise.
*/
bool Sensors::isNetworkComplete() {
	for (int i = 0; i < m_Mesh->addrListTop; i++) {
		if (getArrayIndex(m_Mesh->addrList[i].nodeID) < 0) {
			if (i >= MAX_SENSORS) {
				return true;
			}
			return false;
		}
	}
	return true;
}

/*
Searches the list for sensors with expired lease and if any they are removed
and true is returned. Otherwise and by default false is returned.
*/
bool Sensors::isSensorOffline() {
	bool isExpired = false;
	for (uint8_t i = 0; i < MAX_SENSORS; i++) {
		if ((m_Sensors + i)->id != 0) {
			if (millis() - (m_Sensors + i)->lease > LEASE_TIMEOUT) {
				if ((m_Sensors + i)->type == sensor_types::Sensor_Pir) {
					if (m_PirCounter > 0) {
						m_PirCounter--;
						isExpired = true;
					}
				}
				else if ((m_Sensors + i)->type == sensor_types::Sensor_Magnet) {
					if (m_MagnetCounter > 0) {
						m_MagnetCounter--;
						isExpired = true;
					}
				}
				(m_Sensors + i)->type = sensor_types::Sensor_None;
				(m_Sensors + i)->id = 0;
				(m_Sensors + i)->lease = 0;
			}
		}
	}
	return isExpired;
}

//Resets the counters, ID and clears the list.
void Sensors::reset() {
	m_StartedListening = true;
	m_MagnetCounter = 0;
	m_PirCounter = 0;
	for (uint8_t i = 0; i < MAX_SENSORS; i++) {
		(m_Sensors + i)->type = sensor_types::Sensor_None;
		(m_Sensors + i)->id = 0;
		(m_Sensors + i)->lease = 0;
	}
}

//Returns the magnet sensor count
uint8_t Sensors::getMagnetCount() {
	return m_MagnetCounter;
}

//Returns the pir sensor count
uint8_t Sensors::getPirCount() {
	return m_PirCounter;
}