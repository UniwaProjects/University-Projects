/*
Every ~24 seconds, a ping will be sent defining the sensor's mode.
The sensor will be either armed with the interrupt attached, sleeping for only
2 seconds at a time until s sleep cycle is concluded and able to send triggered
messages or in a disabled state where instead sleeps for 8 seconds at a time until
a sleep cycle conclude, with no interrupts.
*/
#include <LowPower.h>
//Radio network libraries
#include "RF24Network.h"
#include "RF24.h"
#include "RF24Mesh.h"
#include <SPI.h>
#include <EEPROM.h>//Include eeprom.h for AVR (Uno, Nano) etc. except ATTiny

#define REED
//#define PIR
//#define DEBUG

#ifdef REED
const uint8_t HEADER_TYPE = 'M';
const uint8_t NODE_ID = 1;
#endif
#ifdef PIR
const uint8_t HEADER_TYPE = 'P';
const uint8_t NODE_ID = 2;
#endif

//Pin constants
const uint8_t SENSOR_PIN = 2; //Pin D2
const uint8_t VOLTAGE_PIN = A1;
//State constants
const uint8_t PING = 0;
const uint8_t TRIGGERED = 1;
const uint8_t LOW_BATTERY = 2;
//Max sleep cycles 
const uint8_t SLEEP_CYCLES_ARMED = 12;
const uint8_t SLEEP_CYCLES_DISARMED = 3;
//Radio constants
const uint8_t CE_PIN = 9;
const uint8_t CSN_PIN = 10;
const uint8_t CHANNEL = 125; //Sets the frequency to 2525Mhz, above the Wifi range

RF24* g_RadioCom;
RF24Network* g_Network;
RF24Mesh* g_Mesh;
volatile uint8_t g_State;

void resetState();
void setTriggered();
bool sendData();

void setup() {
	Serial.begin(115200);
	pinMode(SENSOR_PIN, INPUT);
	pinMode(VOLTAGE_PIN, INPUT);
	g_RadioCom = new RF24(CE_PIN, CSN_PIN);
	g_Network = new RF24Network(*g_RadioCom);
	g_Mesh = new RF24Mesh(*g_RadioCom, *g_Network);
	g_Mesh->setNodeID(NODE_ID);
	g_Mesh->begin(CHANNEL, RF24_1MBPS, 10000);
	g_RadioCom->setPALevel(RF24_PA_LOW); //Less power needed for RF
}

void loop() {
#ifdef DEBUG
	Serial.print("Sleep cycle time: ");
	Serial.println(millis());
	Serial.flush();
#endif
	resetState();
	if (sendData()) {
		//Attach an interrupt that sets state to triggered if movement is detected.
		attachInterrupt(digitalPinToInterrupt(SENSOR_PIN), setTriggered, HIGH);
		for (uint8_t i = 0; i < SLEEP_CYCLES_ARMED; i++) {
			if (g_State == TRIGGERED) {
				sendData();
				resetState();
				//break;
			}
			LowPower.powerDown(SLEEP_2S, ADC_OFF, BOD_OFF);
		}
	}
	else {
		detachInterrupt(0);//and detach the interrupt before sleeping.
		for (uint8_t i = 0; i < SLEEP_CYCLES_DISARMED; i++) {
			LowPower.powerDown(SLEEP_8S, ADC_OFF, BOD_OFF);
		}
	}
}

void resetState() {
	if (analogRead(A1) <= 494) {
		g_State = LOW_BATTERY;
	}
	else {
		g_State = PING;
	}
}

void setTriggered() {
	g_State = TRIGGERED;
	detachInterrupt(0);
}

bool sendData() {
#ifdef DEBUG
	uint32_t time = millis();
#endif
	g_RadioCom->powerUp();
	g_Mesh->update();

	bool connected = true;
	if (!g_Mesh->checkConnection()) {
		connected = false;
		if (g_Mesh->renewAddress(0) != 0) {
			connected = true;
		}
	}

	bool sent = false;
	uint8_t data = g_State;
	if (connected) {
		sent = g_Mesh->write(&data, HEADER_TYPE, sizeof(data));
		//g_Mesh->releaseAddress();
	}
	g_RadioCom->powerDown();
#ifdef DEBUG
	if (sent) {
		Serial.print("Sent time:");
	}
	else {
		Serial.print("Not Sent time:");
	}
	Serial.println(millis() - time);
	Serial.flush();
#endif // DEBUG
	return sent;
}