#include "Timer.h"
#include "GlobalTypes.h"
#include "Sensors.h"
#include "SavedData.h"
#include "KeyManager.h"
#include "SoundManager.h"
#include "DisplayManager.h"
#include "SpecializedSerial.h"

/*******************
CLASS OBJECTS
*******************/
Sensors* g_Sensors = Sensors::getInstance();
SavedData* g_Data = SavedData::getInstance();
KeyManager* g_Key = KeyManager::getInstance();
SoundManager* g_Sound = SoundManager::getInstance();
DisplayManager* g_Display = DisplayManager::getInstance();
SpecializedSerial* g_Serial = SpecializedSerial::getInstance();

/*******************
CONSTANTS AND ENUMS
*******************/
//Pin related constants
const char DEFAULT_PIN[savedData::PIN_LENGTH] = "1234";
const uint8_t PIN_TIMEOUT_SECS = 10; //Timeout of pin entry
const uint8_t PIN_TRIES_MAX = 1;
const uint8_t SELECT_ARM_TIMEOUT_SECS = 10; //While choosing sensors to be activated
const uint8_t ARM_DELAY_SECS = 30; //Time until all sensors are activated
typedef enum t_UserInput {
	Input_Correct = 0,
	Input_Incorrect = 1,
	Input_Timeout = 2
} t_UserInput;
//Menu related constants
const uint8_t MENU_TIMEOUT_SECS = 5; //If no button is pressed while in a menu, exit after timeout
const uint8_t MENU_TABS_MAX = 5; //Menu tabs number
const uint8_t KEY_TIMEOUT_SECS = 1; //Wifi password letter rotation timeout
//Timer constants
const uint8_t ALERT_DELAY_SECS = 10;
const uint8_t SENSOR_CHECK_SECS = 30;

/*****************
GLOBAL VARIABLES
****************/
//Pin variables
char g_Pin[savedData::PIN_LENGTH] = { 0 };
uint8_t g_CurrentTries = PIN_TRIES_MAX; //Pin tries
//State related variables
Status g_Status = { State_Disarmed, Method_None, Sensor_None_Triggered };
WifiInfo g_WifiInfo = { 0,0,0 };
//Timers
Timer g_InputTimer = Timer(ALERT_DELAY_SECS); //Timer until alert is triggered between sensor input and pin entry
Timer g_SensorTimer = Timer(SENSOR_CHECK_SECS); //Timer to check for deactivated sensors

/*******************
FUNCTIONS
*******************/
void disableAlarm();
void displayStatus();
void keypadListener();
void serialListener();
void sensorListener();
bool sensorsSetup();
void alertState();
bool twoChoiceMenu(uint16_t timeout);
//State change related functions
String getPin(bool hidden);
t_UserInput getInput();
void reduceTries();
void selectArmMethod();
void changeState();
//Main menu functions
bool changePin(const char* newPin);
void(*resetFunc) (void) = 0;
void mainMenu();
//Wifi related functions
uint16_t getFreeRam();
String getWifiPass();
bool connectWifi();
void connectWifiOnBoot();

void setup() {
	g_Display->begin();
	g_Display->showVersion();

	g_Serial->begin();
	g_Sensors->begin();
	strncpy(g_Pin, g_Data->readPin().c_str(), savedData::PIN_LENGTH); //Read EEPROM saved pin
	//Get wifi info or connect to wifi, will only continue after an
	//establised wifi connection
	g_Display->clear();
	g_Display->showWifiConnecting();
	connectWifiOnBoot();
	//A connection must be established for the program to get here
	g_Sound->successTone();
	g_Display->showWifiConnected();
	displayStatus();
}

void loop() {
	serialListener();
	//If system is not on alert
	if (g_Status.state == State_Armed) {
		//Alarm if out of pin tries
		if (g_CurrentTries == 0) {
			alertState();
		}
		sensorListener();
	}
	g_Display->turnOffBacklight(); //Turn off display after timeout
	keypadListener();//Listen for a key
}

//Disables the alarm by reinitializing all the arm related variables and emptying the sensor list
void disableAlarm() {
	g_CurrentTries = PIN_TRIES_MAX;
	g_Status.state = State_Disarmed;
	g_Status.method = Method_None;
	g_Status.sensor = Sensor_None_Triggered;
	g_Sensors->reset();
}

/*
Displays the current state, with or without the pir sensors
depending on the arm method
*/
void displayStatus() {
	uint8_t state = (uint8_t)g_Status.state;
	uint32_t rssi = g_WifiInfo.rssi;
	uint8_t magnetCount = g_Sensors->getMagnetCount();
	uint8_t pirCount = g_Sensors->getPirCount();
	if (g_Status.method == Method_Arm_Away) {
		g_Display->showStatus(state, rssi, magnetCount, pirCount);
	}
	else {
		g_Display->showStatus(state, rssi, magnetCount);
	}
	g_Display->resetBacklightTimer();
}

/*
Listens for a key press, and if its the D key, the pin entry routine to change the state is called.
If the system is disabled and the menu button is pressed, the menu routine is called. In any other
case including the above the lcd is lit and the lcd timer is reset.
*/
void keypadListener() {
	g_Key->getNew();
	if (g_Key->getCurrent() != keys_types::NoKey) {
		g_Display->resetBacklightTimer();
		if (g_Key->dPressed()) {
			g_Sound->menuKeyTone();
			changeState();
		}
		else if (g_Status.state == State_Disarmed) {
			if (g_Key->menuPressed()) {
				g_Sound->menuKeyTone();
				mainMenu();
			}
		}
	}
}

/*
Listens for serial communication activity, which will be wifi disconnections and
state changes.
*/
void serialListener() {
	if (g_Serial->getCommand()) {
		//If the network is not connected
		if (g_Status.state != State_Alert) {
			if (g_Serial->readNetNotConnected()) {
				g_Sound->failureTone();
				g_Display->clear();
				g_Display->resetBacklightTimer();
				g_Display->showWifiNotConnected();
				//Wifi info is reset for the connectWifi
				//to know when new info is received
				g_WifiInfo = { 0,0,0 };
				//Wont continue until a connection has been established
				while (!connectWifi())
					;
				displayStatus();
			}
		}

		Status newStatus = g_Serial->readStatus(g_Status);
		if ((newStatus.state != g_Status.state) || (newStatus.sensor != g_Status.sensor)) {
			g_Sound->successTone();
			g_Display->showStatusChange();
			if (newStatus.state == State_Disarmed) {
				disableAlarm();
			}
			else {
				g_Status = newStatus;
			}
			displayStatus();
		}
		g_Serial->clearSerial();
	}
}

/*
Listens for sensor messages and checks for expired sensors. 
The info of that object is returned and if its a new sensor the screen gets refreshed. 
If the sensor is triggered, the state changes accordingly.
*/
void sensorListener() {
	//If the sensor of state is not triggered
	if (g_Status.sensor == Sensor_None_Triggered) {
		//Listen for sensor messages
		sensor_types::SensorInfo sensor = g_Sensors->listen();
		if (sensor.type != sensor_types::Sensor_None) {
			if (sensor.isNew) {
				displayStatus();
			}
			if (sensor.state == sensor_types::TRIGGERED) {
				if (sensor.type == sensor_types::Sensor_Magnet) {
					g_Status.sensor = Sensor_Magnet_Triggered;
				}
				else if (sensor.type == sensor_types::Sensor_Pir) {
					if (g_Status.method == Method_Arm_Away) {
						g_Status.sensor = Sensor_Pir_Triggered;
					}
				}
				g_InputTimer.reset();
			}
		}
		//Check for offline sensors
		if (g_SensorTimer.timeout()) {
			if (g_Sensors->isSensorOffline()) {
				g_Status.sensor = Sensor_Offline;
				g_InputTimer.reset();
			}
			g_SensorTimer.reset();
		}
	}
	else {
		//If no pin is entered within time
		if (g_InputTimer.timeout()) {
			alertState();
		}
	}
}

/*
Checks the sensor network, registers and displayes info of all sensors that 
make contact. Returns true if all sensors are identified or false otherwise.
*/
bool sensorsSetup() {
	Timer timer(60);
	g_Display->showWaitingSensors();
	while (!timer.timeout()) {
		sensor_types::SensorInfo sensor = g_Sensors->listen();
		//If something is returned besides none
		if (sensor.type != sensor_types::Sensor_None) {
			//If a new sensor is returned
			if (sensor.isNew) {
				bool isBatteryLow = false;
				bool isTriggered = false;
				if (sensor.state == sensor_types::LOW_BATTERY) {
					g_Sound->failureTone();
					isBatteryLow = true;
				}
				else {
					g_Sound->successTone();
				}
				g_Display->showSensorState(sensor.id, isBatteryLow);
				g_Display->addSensorCount(g_Sensors->getMagnetCount(), g_Sensors->getPirCount());
			}
			else {
				if (sensor.state != sensor_types::TRIGGERED) {
					break;
				}
			}
		}
	}
	uint8_t sensorsTotal = g_Sensors->getMagnetCount() + g_Sensors->getPirCount();
	bool networkOnline = false;
	if (sensorsTotal > 0) {
		networkOnline = g_Sensors->isNetworkComplete();
	}
	if (networkOnline) {
		g_Sound->successTone();
		g_Display->showSensorsOK();

	}
	else {
		g_Sound->failureTone();
		g_Display->showSensorsOffline();
	}
	g_Sensors->reset();
	return networkOnline;
}

/*
A state were the alarm beeps, the user is notified of the break in and a pin or a serial command
is expected for the state to change.
*/
void alertState() {
	g_Status.state = State_Alert;
	g_Serial->sendStatus(g_Status);
	displayStatus();
	while (g_Status.state == State_Alert) {
		g_Sound->alarm();
		keypadListener();
		serialListener();
	}
	g_Sound->stopAlarm();
}

//A two choice menu returning true for option A, false for option B and timeout.
bool twoChoiceMenu(uint16_t timeout) {
	Timer timer = Timer(timeout);
	while (1) {
		if (timer.timeout()) {
			g_Sound->menuKeyTone();
			return false;
		}
		else {
			g_Key->getNew();
			if (g_Key->aPressed()) {
				g_Sound->menuKeyTone();
				return true;
			}
			else if (g_Key->bPressed()) {
				g_Sound->menuKeyTone();
				return false;
			}
		}
	}
}

/*
Gets a pin from the user and returns it as a string. If a timeout occurs, a
null string is returned instead.
*/
String getPin(bool hidden) {
	char inputBuffer[savedData::PIN_LENGTH] = { 0 };
	uint8_t arrayIndex = 0;
	Timer timer = Timer(PIN_TIMEOUT_SECS);
	while (1) {
		if (hidden) {
			g_Display->showEnterPin(inputBuffer);
		}
		else {
			g_Display->showEnterNewPin(inputBuffer);
		}

		do {
			if (timer.timeout()) {
				String nullPing = "";
				return nullPing;
			}
			else {
				g_Key->getNew();
			}
		} while (!g_Key->numberPressed() && !g_Key->backspacePressed() && !g_Key->acceptPressed());
		g_Sound->pinKeyTone();

		if (g_Key->numberPressed()) {
			if (arrayIndex < savedData::PIN_LENGTH - 1) {
				char input = g_Key->getCurrent();
				inputBuffer[arrayIndex] = input;
				arrayIndex++;
			}
		}
		else if (g_Key->backspacePressed()) {
			arrayIndex = arrayIndex > 0 ? --arrayIndex : arrayIndex;
			inputBuffer[arrayIndex] = '\0';
		}
		else if (g_Key->acceptPressed() && (arrayIndex == savedData::PIN_LENGTH - 1)) {
			break;
		}
	}
	return String(inputBuffer);
}

/*
Gets a pin from the user and returns a fitting input depending on the result. Timeout for a null
string returned, correct if the pin matches the local pin and incorrect in any other case.
*/
t_UserInput getInput() {
	String pin = getPin(true);
	if (pin.length() < savedData::PIN_LENGTH - 1) {
		return Input_Timeout;
	}
	else if (strncmp(pin.c_str(), g_Pin, savedData::PIN_LENGTH) == 0) {
		return Input_Correct;
	}
	else {
		return Input_Incorrect;
	}
}

//Reduces the pin tries, usually after a wrong attempt.
void reduceTries() {
	g_CurrentTries = g_CurrentTries > 0 ? --g_CurrentTries : g_CurrentTries;
}

/*
The user chooses arm method or the second option is selected automatically after timeout seconds.
If the arm method is arm away, a countdown until full arm is displayed, to give the user time
to exit the house without triggering the alarm.
*/
void selectArmMethod() {
	g_Display->showSensorSelection();
	if (twoChoiceMenu(SELECT_ARM_TIMEOUT_SECS)) {
		g_Status.method = Method_Arm_Away;
		for (uint8_t i = ARM_DELAY_SECS; i > 0; i--) {
			g_Sound->menuKeyTone();
			g_Display->showArmDelay(i);
			delay(1000);
		}
	}
	else {
		g_Status.method = Method_Arm_Stay;
	}
}

/*
Depending on the user input and the current alarm state, the state is changed or the tries
reduced if the input was wrong while the system is armed.
*/
void changeState() {
	t_UserInput inputResult = getInput();
	switch (inputResult) {
	case Input_Correct:
		g_Sound->successTone();
		g_Display->showCorrectPin();
		if (g_Status.state == State_Disarmed) {
			if (sensorsSetup()) {
				g_Status.state = State_Armed;
				selectArmMethod();
				g_SensorTimer.reset();
			}
		}
		else {
			disableAlarm();
		}
		g_Display->showStatusChange();
		g_Serial->sendStatus(g_Status);
		break;
	case Input_Incorrect:
		g_Sound->failureTone();
		g_Display->showIncorrectPin();
		if (g_Status.state == State_Armed) {
			reduceTries();
			//g_Display->showTriesLeft(g_CurrentTries);
		}
		break;
	case Input_Timeout:
		g_Sound->failureTone();
		g_Display->showPinTimeout();
		if (g_Status.state == State_Armed) {
			reduceTries();
			//g_Display->showTriesLeft(g_CurrentTries);
		}
		break;
	}
	displayStatus();
}

/*
Returns true if the pin is changed successfully or false otherwise. For the pin to be changed first
the correct current pin must be entered and if the new pin that was given is of valid length,
the function saves the pin and returns true. In any other case it returns false.
*/
bool changePin(const char* newPin) {
	t_UserInput userInput = getInput();
	switch (userInput) {
	case Input_Correct:
		g_Sound->successTone();
		g_Display->showCorrectPin();
		if (*(newPin + (savedData::PIN_LENGTH - 2)) == 0) {
			return false;
		}
		else {
			strncpy(g_Pin, newPin, savedData::PIN_LENGTH);
		}
		g_Data->savePin(g_Pin);
		g_Sound->successTone();
		g_Display->showChangedPin();
		return true;

	case Input_Incorrect:
		g_Sound->failureTone();
		g_Display->showIncorrectPin();
		return false;

	case Input_Timeout:
		g_Sound->failureTone();
		g_Display->showPinTimeout();
		return false;
	}
}

/*
The user can see in the form of tabs, wifi information, change the wifi network, change the pin,
restore factory settings or reboot the alarm system.
*/
void mainMenu() {
	uint8_t menuTabCount = 0;
	g_Display->showMenuTab(menuTabCount);

	Timer timer = Timer(MENU_TIMEOUT_SECS);
	bool exit = false;
	while (!exit) {
		do {
			g_Key->getNew();
			exit = timer.timeout() ? true : false;
		} while (!g_Key->enterPressed() && !g_Key->nextPressed() && !g_Key->prevPressed() && !exit);
		g_Sound->menuKeyTone();
		timer.reset();

		if (g_Key->enterPressed()) {
			switch (menuTabCount) {
				//Show WiFi info
			case 0:
				g_Display->showWifiInfo(g_WifiInfo.ssid, g_WifiInfo.localIp);
				break;
				//Change wifi network
			case 1:
				g_Display->showConfimation();
				if (twoChoiceMenu(MENU_TIMEOUT_SECS)) {
					if (g_Serial->sendNetChange()) {
						g_WifiInfo = { 0,0,0 };
						while (!connectWifi())
							;
					}
				}
				break;
				//Change pin
			case 2:
				g_Display->showConfimation();
				if (twoChoiceMenu(MENU_TIMEOUT_SECS)) {
					String newPin = getPin(false);
					if (newPin.length() >= savedData::PIN_LENGTH - 1) {
						changePin(newPin.c_str());
					}
				}
				break;
				//Load defaults
			case 3:
				sensorsSetup();
				break;
			case 4:
				g_Display->showConfimation();
				if (twoChoiceMenu(MENU_TIMEOUT_SECS)) {
					changePin(DEFAULT_PIN);
					g_Sound->successTone();
					g_Display->showDefaultsLoaded();
				}
				break;
				//Reset
			case 5:
				g_Display->showConfimation();
				if (twoChoiceMenu(MENU_TIMEOUT_SECS)) {
					if (g_Serial->sendReset()) {
						resetFunc();
					}
				}
				break;
			}
			exit = true;
		}
		else {
			if (g_Key->nextPressed()) {
				menuTabCount = menuTabCount < MENU_TABS_MAX ? ++menuTabCount : menuTabCount;
			}
			else if (g_Key->prevPressed()) {
				if (menuTabCount > 0) {
					menuTabCount--;
				}
				else {
					exit = true;
				}
			}
			g_Display->showMenuTab(menuTabCount);
		}
	}
	displayStatus();
}

//Returns the ammound of free ram in the system.
uint16_t getFreeRam() {
	extern int __heap_start, *__brkval;
	int v;
	int ram = (int)&v - (__brkval == 0 ? (int)&__heap_start : (int)__brkval);
	return ram;
}

/*
A wifi password up to 16 characters is expected from the user, which is then returned.
Pressing the same ABC button twice rotates the alphabet or the symbols if pressed again
in the next second. Numbers are displayed as numbers.
*/
String getWifiPass() {
	g_Display->showEnterWifiPass("");
	char passwordBuffer[WIFI_MAX_LENGTH] = { 0 };
	uint8_t bufferIndex = 0;
	char smallLetter = 'a';
	char capitalLetter = 'A';
	char symbol = '!';
	g_Key->reset();
	Timer timer(KEY_TIMEOUT_SECS);
	while (1) {
		char prevKey = g_Key->getCurrent();
		do {
			g_Key->getNew();
		} while (g_Key->noKeyPressed());

		g_Sound->pinKeyTone();
		if (g_Key->backspacePressed() || g_Key->acceptPressed()) {
			if (g_Key->backspacePressed()) {
				bufferIndex = bufferIndex > 0 ? --bufferIndex : bufferIndex;
				passwordBuffer[bufferIndex] = 0;
			}
			else if (g_Key->acceptPressed()) {
				break;
			}
		}
		else {
			if (bufferIndex < WIFI_MAX_LENGTH - 1) {
				if (g_Key->aPressed()) {
					if (prevKey == g_Key->getCurrent() && !timer.timeout()) {
						smallLetter = smallLetter == 'z' ? 'a' : ++smallLetter;
						bufferIndex = bufferIndex > 0 ? --bufferIndex : bufferIndex;
					}
					else {
						smallLetter = 'a';
					}
					passwordBuffer[bufferIndex] = smallLetter;
					timer.reset();
				}
				else if (g_Key->bPressed()) {
					if (prevKey == g_Key->getCurrent() && !timer.timeout()) {
						capitalLetter = capitalLetter == 'Z' ? 'A' : ++capitalLetter;
						bufferIndex = bufferIndex > 0 ? --bufferIndex : bufferIndex;
					}
					else {
						capitalLetter = 'A';
					}
					passwordBuffer[bufferIndex] = capitalLetter;
					timer.reset();
				}
				else if (g_Key->cPressed()) {
					if (prevKey == g_Key->getCurrent() && !timer.timeout()) {
						switch (symbol) {
						case '/':
							symbol = ':';
							break;
						case '@':
							symbol = '[';
							break;
						case '`':
							symbol = '{';
							break;
						case '}':
							symbol = '!';
							break;
						default:
							symbol++;
							break;
						}
						bufferIndex = bufferIndex > 0 ? --bufferIndex : bufferIndex;
					}
					else {
						symbol = '!';
					}
					passwordBuffer[bufferIndex] = symbol;
					timer.reset();
				}
				else {
					passwordBuffer[bufferIndex] = g_Key->getCurrent();
				}
				bufferIndex++;
			}
		}
		g_Display->showEnterWifiPass(passwordBuffer);
	}
	return String(passwordBuffer);
}

void connectWifiOnBoot() {
	do {
		//Get a command, the loop ensure that the command wont be ill timed and missed
		if (g_Serial->getCommand()) {
			//If its the not connected command
			if (g_Serial->readNetNotConnected()) {
				g_Sound->failureTone();
				g_Display->showWifiNotConnected();
				//Tries to connect, will store wifi info if connected
				//breaking the whole loop, or return false and try again
				while (!connectWifi())
					;
			}
			//Else get the wifi info
			else {
				g_WifiInfo = g_Serial->readNetInfo();
			}
			//Clear the serial before starting again
			g_Serial->clearSerial();
		}
	} while (g_WifiInfo.rssi == 0);
}

/*
Returns true for a successful connection with a wifi network, which happens in collaboration with
the serial class. After the esp receives a request to change the network a network list is
returned and ram gets allocated for a network list, the size of which depends in the currently
free ram. After the user chooses a network and types the password an attempt for connection is made.
If the attempt is successful the function returns true or false otherwise.
*/
bool connectWifi() {
	g_Display->showScanWifi();
	while (!g_Serial->getCommand())
		;
	int8_t networksCount = g_Serial->readNetworkHeader();
	g_Serial->clearSerial();
	//If there are available networks
	if (networksCount > 0) {
		//Dynamically allocate a list of networks
		uint8_t listSize = (getFreeRam() - 200) / sizeof(WifiNetwork);//Leave 200 bytes of memory free
		WifiNetwork* networks = (WifiNetwork *)malloc(listSize * sizeof(WifiNetwork));
		if (listSize < networksCount) {
			networksCount = listSize;
		}
		//Read each network
		uint8_t networkIndex = 0;
		bool done = false;
		do {
			if (g_Serial->getCommand()) {
				if (g_Serial->readNetworkEnd()) {
					done = true;
				}
				else {
					WifiNetwork network = g_Serial->readNetwork();
					//If more networks than allowed are received, replace the networks
					//with weaker signal
					if (networkIndex >= networksCount) {
						uint8_t smallestRssiIndex = 0;
						for (uint8_t i = 0; i < listSize; i++) {
							if ((networks + smallestRssiIndex)->rssi < (networks + i)->rssi) {
								smallestRssiIndex = i;
							}
						}
						if ((networks + smallestRssiIndex)->rssi < network.rssi) {
							*(networks + smallestRssiIndex) = network;
						}
					}
					//Otherwise all the network to the list
					else {
						*(networks + networkIndex) = network;
						networkIndex++;
					}
				}
				g_Serial->clearSerial();
			}
		} while (!done);

		//Make a menu from the networks for the user to choose
		networkIndex = 0;
		while (1) {
			//Display network depending on index
			bool isFirst = networkIndex == 0 ? true : false;
			bool isLast = (networksCount - 1) == networkIndex ? true : false;
			g_Display->showWifiNetwork((networks + networkIndex)->ssid, (networks + networkIndex)->rssi, isFirst, isLast);
			//Listen for a key
			do {
				g_Key->getNew();
			} while (!g_Key->enterPressed() && !g_Key->nextPressed() && !g_Key->prevPressed());
			g_Sound->menuKeyTone();

			//If enter is pressed
			if (g_Key->enterPressed()) {
				//Display the network encryption
				g_Display->showWifiEncryption((networks + networkIndex)->encryption);
				//Get a password from the user
				char passBuffer[WIFI_MAX_LENGTH] = { 0 };
				if ((networks + networkIndex)->encryption != Encrytpion_NONE) {
					strncpy(passBuffer, getWifiPass().c_str(), WIFI_MAX_LENGTH - 1);
				}
				//Send the credentials
				while (!g_Serial->sendNetCredentials((networks + networkIndex)->ssid, passBuffer))
					;
				g_Display->showWifiSsid((networks + networkIndex)->ssid);
				g_Display->showWifiConnecting();
				//Either get wifi info or a not connected message
				do {
					if (g_Serial->getCommand()) {
						if (g_Serial->readNetNotConnected()) {
							g_Sound->failureTone();
							g_Display->showWifiNotConnected();
							free(networks);
							return false;
						}
						else {
							g_WifiInfo = g_Serial->readNetInfo();
							g_Sound->successTone();
							g_Display->showWifiConnected();
							free(networks);
							return true;
						}
						g_Serial->clearSerial();
					}
				} while (g_WifiInfo.rssi == 0);
			}
			else if (g_Key->nextPressed()) {
				networkIndex = networkIndex < networksCount - 1 ? ++networkIndex : networkIndex;
			}
			else if (g_Key->prevPressed()) {
				networkIndex = networkIndex > 0 ? --networkIndex : networkIndex;
			}
		}
	}
	g_Display->showNoWifiFound();
	return false;
}
