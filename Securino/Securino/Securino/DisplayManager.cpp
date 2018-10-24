#include "DisplayManager.h"
#include "FlashStrings.h"

DisplayManager* DisplayManager::m_Instance = nullptr;

DisplayManager* DisplayManager::getInstance() {
	if (m_Instance == nullptr) {
		m_Instance = new DisplayManager();
	}
	return m_Instance;
}

DisplayManager::DisplayManager() {
	m_Lcd = new LiquidCrystal_I2C(LCD_ADDRESS, EN, RW, RS, D4, D5, D6, D7, BACKLIGHT, POSITIVE);
}

/*
In begin the lcd object is set up, first by calling lcd's begin,
then setting backlight to high and then creating custom symbols
for the wifi representation in the form of 8 lines with 5 columns each.
Setting a binary value of 0 or 1 in that column lights the pixel white
or leaves it non lit. For a more reprehensive represantation 2 lcd columns
are used to paint a wifi signal strength bar resembling a mobile phone's
GSM signal bars.
*/
void DisplayManager::begin() {
	m_Lcd->begin(LCD_COLUMNS, LCD_LINES);
	m_Lcd->setBacklight(HIGH);
	uint8_t maxSingalPt1[8] = { B00000, B00000, B00000, B00000, B00001, B00001, B01001, B01001 };
	m_Lcd->createChar(1, maxSingalPt1);
	uint8_t maxSignalPt2[8] = { B00001, B00001, B01001, B01001, B01001, B01001, B01001, B01001 };
	m_Lcd->createChar(2, maxSignalPt2);
	uint8_t weakSignalPt1[8] = { B00000, B00000, B00000, B00000, B00001, B00000, B01000, B01001 };
	m_Lcd->createChar(3, weakSignalPt1);
	uint8_t weakSignalPt2[8] = { B00001, B00000, B01000, B01000, B01000, B01000, B01000, B01001 };
	m_Lcd->createChar(4, weakSignalPt2);
	uint8_t noSignalPt1[8] = { B00000, B00000, B00000, B00000, B00001, B00000, B00000, B01001 };
	m_Lcd->createChar(5, noSignalPt1);
	uint8_t noSignalPt2[8] = { B00001, B00000, B01000, B00000, B00000, B00000, B00000, B01001 };
	m_Lcd->createChar(6, noSignalPt2);
}

/*
A timer is used for the backlight timeout, when reset
the backlight is turned on and the timer is reset
*/
void DisplayManager::resetBacklightTimer() {
	m_Lcd->backlight();
	m_BacklightTimer.reset();
}

/*
If the timeout time has passed without a reset then the
backlight is turned off.
*/
void DisplayManager::turnOffBacklight() {
	if (m_BacklightTimer.timeout()) {
		m_Lcd->noBacklight();
	}
}

void DisplayManager::clear() {
	m_Lcd->clear();
}

//Displays product brand
void DisplayManager::showVersion() {
	clear();
	centerPrintFlashText(text::VERSION);
	delay(STANDARD_DELAY);
}

//Displays state of alarm
void DisplayManager::showStatus(const uint8_t state, const int32_t rssi, const int8_t magnetCount, const int8_t pirCount) {
	clear();
	switch (state) {
	case 0:
		m_Lcd->print(FString(text::DISARMED));
		m_Lcd->setCursor(10, 0);
		m_Lcd->print(FString(text::WIFI));
		addWifiSignal(rssi); //RSSI value is tested for integrity inside showWifiSignal
		m_Lcd->setCursor(0, 1);
		m_Lcd->print(FString(text::MENU));
		m_Lcd->setCursor(10, 1);
		m_Lcd->print(FString(text::PIN));
		break;
	case 1:
		m_Lcd->print(FString(text::ARMED));
		m_Lcd->setCursor(10, 0);
		m_Lcd->print(FString(text::WIFI));
		addWifiSignal(rssi); //RSSI value is tested for integrity inside showWifiSignal
		addSensorCount(magnetCount, pirCount);
		break;
	case 2:
		showAlarmTriggered();
		break;
	}
}

//Adds a sensor display on second row of lcd
void DisplayManager::addSensorCount(const int8_t magnetCount, const int8_t pirCount) {
	m_Lcd->setCursor(0, 1);
	if (magnetCount >= 0) {
		m_Lcd->print(FString(text::MAGNET));
		m_Lcd->print(magnetCount);
	}
	if (pirCount >= 0) {
		m_Lcd->print(FString(text::PIR));
		m_Lcd->print(pirCount);
	}
}

//Displays a changing state message
void DisplayManager::showStatusChange() {
	clear();
	m_Lcd->print(FString(text::STATE_CHANGE));
}

//Displays an alarm triggered message
void DisplayManager::showAlarmTriggered() {
	clear();
	m_Lcd->print(FString(text::ALERT_TRIGGERED));
	m_Lcd->backlight();
}

//Displays a menu tab depending on the tab number
void DisplayManager::showMenuTab(const uint8_t currentTab) {
	clear();
	switch (currentTab) {
	case 0:
		centerPrintFlashText(text::MENU_WIFI_INFO);
		break;
	case 1:
		centerPrintFlashText(text::MENU_CHANGE_WIFI);
		break;
	case 2:
		centerPrintFlashText(text::MENU_CHANGE_PIN);
		break;
	case 3:
		centerPrintFlashText(text::SETUP_SENSORS);
		break;
	case 4:
		centerPrintFlashText(text::MENU_LOAD_DEFAULTS);
		break;
	case 5:
		centerPrintFlashText(text::MENU_RESET);
	}
	m_Lcd->setCursor(0, 1);
	m_Lcd->print(ARROW_LEFT);
	uint8_t maxTabs = 5;
	if (currentTab < maxTabs) {
		m_Lcd->print(FString(text::MENU_KEYS));
		m_Lcd->print(ARROW_RIGHT);
	}
	else {
		m_Lcd->print(FString(text::MENU_KEYS_LAST));
	}
}

//Displays a confirmation message
void DisplayManager::showConfimation() {
	clear();
	centerPrintFlashText(text::PROCEED_LINE_1);
	m_Lcd->setCursor(0, 1);
	centerPrintFlashText(text::PROCEED_LINE_2);
}

//Displays a defauls loaded successful message
void DisplayManager::showDefaultsLoaded() {
	clear();
	centerPrintFlashText(text::DEFAULTS_LOADED);
	delay(STANDARD_DELAY);
}

void DisplayManager::showWaitingSensors() {
	clear();
	m_Lcd->print(FString(text::TESTING_SENSORS));
}

//Shows sensor state on first row
void DisplayManager::showSensorState(uint8_t id, bool isBatteryLow) {
	clear();
	if (isBatteryLow) {
		m_Lcd->print(FString(text::BATTERY_LOW));
		m_Lcd->print(id);
	}
	else {
		m_Lcd->print(FString(text::SENSOR));
		m_Lcd->print(FString(text::ID));
		m_Lcd->print(id);
		m_Lcd->print(FString(text::OK));
	}
}

//Shows sensor ok on first row
void DisplayManager::showSensorsOK() {
	clear();
	m_Lcd->print(FString(text::SENSOR));
	m_Lcd->print(FString(text::NET));
	m_Lcd->print(FString(text::OK));
	delay(STANDARD_DELAY);
}

//Shows sensors offline
void DisplayManager::showSensorsOffline() {
	clear();
	m_Lcd->print(FString(text::SENSORS_OFFLINE));
	delay(STANDARD_DELAY);
}

//Asks for a pin, characters appears as *
void DisplayManager::showEnterPin(const char * pin) {
	clear();
	m_Lcd->print(FString(text::ENTER_PIN));
	uint8_t currentPinLength = strlen(pin);
	for (uint8_t i = 0; i < currentPinLength; i++) {
		m_Lcd->print('*');
	}
	showInputOptions();
}

/*
Asks for a new pin, characters are
displayed normally for the user to see his input
*/
void DisplayManager::showEnterNewPin(const char * pin) {
	clear();
	m_Lcd->print(FString(text::NEW_PIN));
	m_Lcd->print(pin);
	showInputOptions();
	m_Lcd->setCursor(9, 0);
}

//Displays a pin changed message
void DisplayManager::showChangedPin() {
	clear();
	centerPrintFlashText(text::PIN_CHANGED);
	delay(STANDARD_DELAY);
}

//Displays a correct message
void DisplayManager::showCorrectPin() {
	clear();
	centerPrintFlashText(text::CORRECT);
	delay(STANDARD_DELAY);
}

//Displays a incorrect pin message
void DisplayManager::showIncorrectPin() {
	clear();
	centerPrintFlashText(text::INCORRECT);
	delay(STANDARD_DELAY);
}

//Displays a pin timeout message
void DisplayManager::showPinTimeout() {
	clear();
	centerPrintFlashText(text::TIMED_OUT);
	delay(STANDARD_DELAY);
}

/*
Adds a tries left message on the second row
of a failed pin attempt
*/
/*void DisplayManager::showTriesLeft(const uint8_t tries) {
	m_Lcd->setCursor(0, 1);
	m_Lcd->print(FString(text::TRIES_LEFT));
	m_Lcd->print(tries);
	delay(STANDARD_DELAY);
}*/

//Displays a arm method menu
void DisplayManager::showSensorSelection() {
	clear();
	m_Lcd->print(FString(text::ARM_SELECT_LINE_1));
	m_Lcd->setCursor(0, 1);
	m_Lcd->print(FString(text::ARM_SELECT_LINE_2));
}

//Displays an alarm arm delay message
void DisplayManager::showArmDelay(const uint8_t seconds) {
	clear();
	m_Lcd->print(seconds);
	m_Lcd->print(FString(text::ARM_DELAY_LINE_1));
	m_Lcd->setCursor(0, 1);
	m_Lcd->print(FString(text::ARM_DELAY_LINE_2));
}

//Displays the ssid of a network
void DisplayManager::showWifiSsid(const char * ssid) {
	clear();
	m_Lcd->print(FString(text::SSID));
	m_Lcd->print(" ");
	m_Lcd->print(ssid);
}

//Displays wifi connecting on row 2
void DisplayManager::showWifiConnecting() {
	m_Lcd->setCursor(0, 1);
	centerPrintFlashText(text::WIFI_CONNECTING);
	delay(STANDARD_DELAY);
}

//Displays wifi connected on row 2
void DisplayManager::showWifiConnected() {
	m_Lcd->setCursor(0, 1);
	centerPrintFlashText(text::WIFI_CONNECTED);
	delay(STANDARD_DELAY);
}

//Displays wifi disconnected on row 2
void DisplayManager::showWifiNotConnected() {
	m_Lcd->setCursor(0, 1);
	centerPrintFlashText(text::WIFI_DISCONNECT);
	delay(STANDARD_DELAY);
}

//Displays a scanned wifi network info
void DisplayManager::showWifiNetwork(const char* ssid, const int32_t rssi, bool isFirst, bool isLast) {
	clear();
	m_Lcd->print(ssid);
	m_Lcd->setCursor(14, 0);
	addWifiSignal(rssi);
	m_Lcd->setCursor(0, 1);
	if (isFirst && isLast) {
		centerPrintFlashText(text::WIFI_CONNECT_KEYS_SINGLE_NET);
	}
	else if (isFirst) {
		m_Lcd->print(FString(text::WIFI_CONNECT_KEYS_FIRST));
		m_Lcd->print(ARROW_RIGHT);
	}
	else if (isLast) {
		m_Lcd->print(ARROW_LEFT);
		m_Lcd->print(FString(text::WIFI_CONNECT_KEYS_LAST));
	}
	else {
		m_Lcd->print(ARROW_LEFT);
		m_Lcd->print(FString(text::WIFI_CONNECT_KEYS));
		m_Lcd->print(ARROW_RIGHT);
	}
}

//Displays a wifi not found message
void DisplayManager::showNoWifiFound() {
	clear();
	centerPrintFlashText(text::NO_NETWORKS_LINE_1);
	m_Lcd->setCursor(0, 1);
	centerPrintFlashText(text::NO_NETWORKS_LINE_2);
	delay(STANDARD_DELAY);
}

/*
Displays the encryption of the wifi network
that the user is trying to connect to.
*/
void DisplayManager::showWifiEncryption(const uint8_t encryption) {
	clear();
	m_Lcd->print(FString(text::ENCRYPTION));
	switch (encryption) {
	case 2:
		m_Lcd->print(FString(text::ENCR_WPA));
		break;
	case 4:
		m_Lcd->print(FString(text::ENCR_WPA2));
		break;
	case 5:
		m_Lcd->print(FString(text::ENCR_WEP));
		break;
	case 7:
		m_Lcd->print(FString(text::ENCR_NONE));
		break;
	case 8:
		m_Lcd->print(FString(text::ENCR_AUTO));
		break;
	}
	m_Lcd->setCursor(0, 1);
	m_Lcd->print(FString(text::CHARACTER_LIMIT));
	delay(EXTENDED_DELAY);
}

//Display a wifi info message
void DisplayManager::showWifiInfo(const char* ssid, const char* ip) {
	clear();
	centerPrintFlashText(text::SSID);
	m_Lcd->setCursor(0, 1);
	centerPrintText(ssid);
	delay(EXTENDED_DELAY);

	clear();
	centerPrintFlashText(text::LOCAL_IP);
	m_Lcd->setCursor(0, 1);
	centerPrintText(ip);
	delay(EXTENDED_DELAY);
}

//Displays the wifi pass entered by the user
void DisplayManager::showEnterWifiPass(const char * pass) {
	clear();
	m_Lcd->print(pass);
	m_Lcd->setCursor(0, 1);
	m_Lcd->print(FString(text::WIFI_PASS_KEYS));
	m_Lcd->setCursor(0, 0);
}

//Display a wifi scan message
void DisplayManager::showScanWifi() {
	clear();
	m_Lcd->print(FString(text::WIFI_SCANNING));
}

//Prints flash text in the center of the lcd
void DisplayManager::centerPrintFlashText(const char* text) {
	uint8_t textLength = strlen_P(text);
	uint8_t spaces = (LCD_COLUMNS - textLength) / 2;
	for (uint8_t i = 0; i < spaces; i++) {
		m_Lcd->print(' ');
	}
	m_Lcd->print(FString(text));
}

//Prints plain char text in the center of the screen
void DisplayManager::centerPrintText(const char* text) {
	uint8_t textLength = strlen(text);
	uint8_t spaces = (LCD_COLUMNS - textLength) / 2;
	for (uint8_t i = 0; i < spaces; i++) {
		m_Lcd->print(' ');
	}
	m_Lcd->print(text);
}

//Display options while a pin is typed
void DisplayManager::showInputOptions() {
	m_Lcd->setCursor(0, 1);
	m_Lcd->print(FString(text::PIN_KEYS));
}

/*
Displays wifi signal in a two custom char
bar form, depending on the RSSI number
*/
void DisplayManager::addWifiSignal(const int32_t dbm) {
	if (dbm >= -30) {
		m_Lcd->write(uint8_t(1));
		m_Lcd->write(uint8_t(2));
	}
	else if (dbm >= -67) {
		m_Lcd->write(uint8_t(1));
		m_Lcd->write(uint8_t(4));
	}
	else if (dbm >= -70) {
		m_Lcd->write(uint8_t(1));
		m_Lcd->write(uint8_t(6));
	}
	else if (dbm >= -80) {
		m_Lcd->write(uint8_t(3));
		m_Lcd->write(uint8_t(6));
	}
	else {
		m_Lcd->write(uint8_t(5));
		m_Lcd->write(uint8_t(6));
	}
}