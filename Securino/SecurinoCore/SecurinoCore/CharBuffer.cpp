#include "CharBuffer.h"
/*
The object is created by specifying the size of the buffer.
The size cannot be larger than the bufferSize's variable size,
so if thats the case, the buffer is initialized in the max allowed
size.
*/
CharBuffer::CharBuffer(const uint16_t requestedSize) {
	//Max size is bufferSize's variable's max capacity
	uint16_t maxBufferSize = pow(2, (sizeof(m_BufferSize) * 8));
	//If the size is larger the maximum allowed size
	m_BufferSize = requestedSize > maxBufferSize ? maxBufferSize : requestedSize;
	//Dynamically allocated the required memory.
	m_CharBuffer = (char *)malloc(m_BufferSize * sizeof(char));
	//Clear fills the buffer with zeroes, which is good for initialization
	clear();
}

/*
Since malloc was used we need to free the memory when
the object is terminated.
*/
CharBuffer::~CharBuffer() {
	free(m_CharBuffer);
}

/*
Returns the character of the speciafied position if that position
is withing buffer's size limits. Otherwise 0 is returned.
*/
char CharBuffer::getChar(const uint16_t index) {
	if (index > m_BufferSize) {
		return 0;
	}
	else {
		char character = *(m_CharBuffer + index);
		return character;
	}
}

/*
Returns an integer of the item in the specified position. If the position
is outsize buffer's size limits, -1 is returned.
*/
int8_t CharBuffer::getInt(const uint16_t index) {
	if (index > m_BufferSize) {
		return -1;
	}
	else {
		uint8_t number = *(m_CharBuffer + index) - '0';
		return number;
	}
}
/*
If the position is within limits, it sets that memory cell to
the specified character and true is returned. If the position
is out of limits false is returned.
*/
bool CharBuffer::setChar(const uint16_t index, char character) {
	if (index > m_BufferSize) {
		return false;
	}
	else {
		*(m_CharBuffer + index) = character;
		return true;
	}
}

/*
Comapares the word with the first x letters of the buffer,
where x is the size of the word. If the size of the word is within
limits, the comparison takes place and depending on the outcome,
true is returned for a match and false in any other case.
*/
bool CharBuffer::find(const char * word) {
	//A larger number size is used for string length to avoid 
	//number overflows.
	uint16_t wordLength = strlen(word);
	//If the word length is larger than the buffer,
	//false is returned by default.
	if (wordLength > m_BufferSize) {
		return false;
	}
	//Else a comparison takes place and true or false is returned
	//depending on the outcome.
	else {
		if (strncmp(m_CharBuffer, word, wordLength) == 0)
			return true;
		else
			return false;
	}
}
/*
Fills the buffer's addresses with zeroes
*/
void CharBuffer::clear() {
	for (uint8_t i = 0; i < m_BufferSize; i++) {
		m_CharBuffer[i] = 0;
	}
}

String CharBuffer::toString() {
	return String(m_CharBuffer);
}