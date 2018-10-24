/*
	Handles the communication for the Ubidots IOT platform. Responsible for the upload 
and download of Ubidot's variables, as well as the wifi connection. The IDs need to
match those of the corresponding Ubidots variables.
*/
#pragma once

#include "UbidotsMicroESP8266.h"
#include "GlobalTypes.h"
//#define DEFAULT_DEVICE_NAME "Securino"

class UbidotsManager {
public:
	UbidotsManager(UbidotsManager const&) = delete;
	void operator=(UbidotsManager const&) = delete;
	static UbidotsManager* getInstance();
	bool sendStatus(Status currentStatus);
	Status getStatus(const Status& currentStatus);
	bool wifiConnect(char* ssid, char* pass);

private:
	static UbidotsManager* instance;
	UbidotsManager();
	~UbidotsManager();
	const uint8_t CONNECTION_RETRIES = 15; //How many times to retry for a connection
	const uint16_t CONNECTION_RETRY_DELAY = 1000; //Delay between retries
	//Ubidots variables, used for communicating with ubidots
	char* TOKEN = "Jhr228UUlzED3hsVRlsKYMWYFb24WE";
	char* STATE_ID = "5aaea8147625423216765f26";
	char* METHOD_ID = "5aaea8147625423216765f28";
	char* SENSOR_ID = "5aaea8147625423216765f27";
	Ubidots* client;
};