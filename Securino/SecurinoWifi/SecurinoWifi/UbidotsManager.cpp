#include "UbidotsManager.h"

UbidotsManager* UbidotsManager::instance = nullptr;

UbidotsManager* UbidotsManager::getInstance() {
	if (instance == nullptr) {
		instance = new UbidotsManager();
	}
	return instance;
}

UbidotsManager::UbidotsManager() {
	client = new Ubidots(TOKEN);
}

UbidotsManager::~UbidotsManager() {
	free(client);
}

/*
A connection attempt is made, with the given retries and intervals between them.
*/
bool UbidotsManager::wifiConnect(char* ssid, char* pass) {
	return client->wifiConnection(ssid, pass, CONNECTION_RETRIES, CONNECTION_RETRY_DELAY);
}

/*
Ubidots only accepts float type values. The state will be converted
to float before sending and to state after being received.
*/
bool UbidotsManager::sendStatus(Status currentStatus) {
	client->add(STATE_ID, (float)currentStatus.state);
	client->add(METHOD_ID, (float)currentStatus.method);
	client->add(SENSOR_ID, (float)currentStatus.sensor);
	bool sent = client->sendAll();
	return sent;
}

/*
Gets the variables from ubidots, they get converted to state the new state is
returned. If there was an error in getting the variables, an error code
is received. If that happens the old state is returned instead so that
the program won't crash.
*/
Status UbidotsManager::getStatus(const Status& currentStatus) {
	Status newStatus = { State_Disarmed, Method_None, Sensor_None_Triggered };
	float stateValue = client->getValue(STATE_ID);
	float sensorValue = client->getValue(METHOD_ID);
	float methodValue = client->getValue(SENSOR_ID);

	if ((stateValue != ERROR_VALUE)
		&& (sensorValue != ERROR_VALUE)
		&& (methodValue != ERROR_VALUE)) {
		newStatus.state = (t_AlarmState)stateValue;
		newStatus.method = (t_ArmMethod)sensorValue;
		newStatus.sensor = (t_SensorTriggered)methodValue;
		return newStatus;
	}
	return currentStatus;
}