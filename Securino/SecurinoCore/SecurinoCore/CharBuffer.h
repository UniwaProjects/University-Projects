/*
	A char buffer which is meant to hold the contents of the serial buffer since
the serial buffer is cleared upon reading and can't be read multiple times.
*/
#pragma once

#include <Arduino.h>

class CharBuffer {
  public:
    CharBuffer(const uint16_t requestedSize);
    ~CharBuffer();
    char getChar(const uint16_t index);
    int8_t getInt(const uint16_t index);
    bool setChar(const uint16_t index, const char character);
    bool find(const char* word);
	String toString();
    void clear();

  private:
    uint8_t m_BufferSize;
    char * m_CharBuffer;
};