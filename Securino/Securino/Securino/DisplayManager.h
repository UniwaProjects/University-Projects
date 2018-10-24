/*
	The role of this class is to manage all of the visual feedback displayed on the 
i2c lcd as well as the timeout of the backlight. Handles formatting the text in the
center of the screen when needed and reading flash strings to display.
*/
#pragma once

#include <Arduino.h>
#include <Wire.h>
#include <LCD.h>
#include <LiquidCrystal_I2C.h>
#include "Timer.h"

class DisplayManager {
public:
	DisplayManager(DisplayManager const&) = delete;
	void operator=(DisplayManager const&) = delete;
	static DisplayManager* getInstance();
	void begin();
	void resetBacklightTimer();
	void turnOffBacklight();
	void clear();
	//Functions that print on lcd
	void showVersion();
	void showStatus(const uint8_t state, const int32_t rssi, const int8_t magnetCount, const int8_t pirCount = -1);
	void addSensorCount(const int8_t magnetCount, const int8_t pirCount);
	void showStatusChange();
	void showAlarmTriggered();
	//Menu messages
	void showMenuTab(uint8_t tab);
	void showConfimation();
	void showWaitingSensors();
	void showSensorState(uint8_t id, bool isBatteryLow);
	void showSensorsOK();
	void showSensorsOffline();
	void showDefaultsLoaded();
	// Pin/Pass messages
	void showEnterPin(const char* pin);
	void showEnterNewPin(const char* pin);
	void showChangedPin();
	void showCorrectPin();
	void showIncorrectPin();
	void showPinTimeout();
	//void showTriesLeft(uint8_t tries);
	//Sensor related messages
	void showSensorSelection();
	void showArmDelay(uint8_t seconds);
	// Wifi related messages
	void showWifiSsid(const char* ssid);
	void showWifiConnecting();
	void showWifiConnected();
	void showWifiNotConnected();
	void showWifiNetwork(const char* ssid, const int32_t rssi, const bool firstNetwork, const bool lastNetwork);
	void showNoWifiFound();
	void showWifiEncryption(uint8_t encryption);
	void showWifiInfo(const char* ssid, const char* ip);
	void showEnterWifiPass(const char* pass);
	void showScanWifi();

private:
	static DisplayManager* m_Instance;
	DisplayManager();
	//LCD object
	const uint8_t LCD_ADDRESS = 0x27; //I2C address of the lcd
	const uint8_t EN = 2; //Pin EN on the I2C chip
	const uint8_t RW = 1; //Pin RW on the I2C chip
	const uint8_t RS = 0; //Pin RS on the I2C chip
	const uint8_t D4 = 4; //Pin D4 on the I2C chip
	const uint8_t D5 = 5; //Pin D5 on the I2C chip
	const uint8_t D6 = 6; //Pin D6 on the I2C chip
	const uint8_t D7 = 7; //Pin D7 on the I2C chip
	const uint8_t BACKLIGHT = 3; //Backlight pin on the I2C chip
	const uint8_t LCD_COLUMNS = 16; //Columns of the lcd module
	const uint8_t LCD_LINES = 2; //Lines of the lcd module
	LiquidCrystal_I2C* m_Lcd;
	//Timer
	const uint16_t STANDARD_DELAY = 800; //Delay of screen messages
	const uint16_t EXTENDED_DELAY = 3000; //Delay of screen messages
	const uint16_t TIMEOUT_SECONDS = 5; //Timeout of lcd backlight
	Timer m_BacklightTimer = Timer(TIMEOUT_SECONDS);
	//Aiding functions for the show functions
	const char ARROW_RIGHT = 126; //ASCII number for right arrow
	const char ARROW_LEFT = 127; //ASCII number for left arrow
	void centerPrintFlashText(const char* text);
	void centerPrintText(const char* text);
	void addWifiSignal(const int32_t dbm);
	void showInputOptions();
};