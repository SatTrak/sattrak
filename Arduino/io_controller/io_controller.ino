#include <stdlib.h>
#include <dht.h>
#include "serial_comm.h"

// ======================= 
// CONSTANTS
// =======================

// Basic constants
const int BAUD_RATE = 9600;
const int LED_PIN = 13;
const int DHT22_PIN = 5;

//// Packet constants
//const int PACKET_LEN = 24; // bytes
//const byte HEADER = 0xAA;
//const int LOC_HEADER = 0;
//const int LOC_COMMAND = 1;
//const int LOC_ARGS = 2;
//
//// Commands and arguments
//const byte COMMAND_ACK = 0x01;
//const int LOC_ACKD_COMMAND = 0;
//const int LEN_ACKD_COMMAND = 1;
//
//const byte COMMAND_NACK = 0x02;
//const int LOC_NACKD_COMMAND = 0;
//const int LEN_NACKD_COMMAND = 1;
//
//const byte COMMAND_ORNT_SET = 0x03;
//const int LOC_AZIMUTH = 0;
//const int LEN_AZIMUTH = 8;
//const int LOC_ELEVATION = 8;
//const int LEN_ELEVATION = 8;
//
//const byte COMMAND_ORNT_READ = 0x04;
//// No args
//
//const byte COMMAND_ORNT_RESPONSE = 0x05;
//// Same args as COMMAND_ORNT_SET
//
//const byte COMMAND_ENV_READ = 0x06;
//// No args
//
//const byte COMMAND_ENV_RESPONSE = 0x07;
//const int LOC_TEMP = 0;
//const int LEN_TEMP = 8;
//const int LOC_HUMID = 8;
//const int LEN_HUMID = 8;
//
//const byte COMMAND_GPS_READ = 0x08;
//// No args
//
//const byte COMMAND_GPS_RESPONSE = 0x09;
//const int LOC_LAT = 0;
//const int LEN_LAT = 8;
//const int LOC_LONG = 8;
//const int LEN_LONG = 8;
//
//const byte COMMAND_EST_CONN = 0x0A;
//// No args


// ======================= 
// INSTANCE VARIABLES
// =======================

// Temp/humidity
dht DHT;
double temperature;
double humidity;


// ======================= 
// STANDARD FUNCTIONS
// =======================

void setup() {
  // Set LED pin to output for debugging
  pinMode(LED_PIN, OUTPUT);

  // Start serial communication at BAUD_RATE
  Serial.begin(BAUD_RATE);
  delay(100);
}

void loop() {
  // Initialize a byte array to read in a packet
  char rx_bytes[PACKET_LEN];

  int num_bytes = Serial.readBytes(rx_bytes, PACKET_LEN);

  // If data was received, handle the packet
  if (num_bytes > 0) {
    set_led(true);
    handle_packet((byte *) rx_bytes);
  } 
  else {
    set_led(false);
  }

}


// ======================= 
// COMMUNICATION FUNCTIONS
// =======================

// Handle a received packet
void handle_packet(byte *packet_bytes) {

  // Do nothing if the packet doesn't have the right header
  if (!has_header(packet_bytes)) {
    return;
  }

  byte command = packet_bytes[LOC_COMMAND];

  switch (command) {
    case (COMMAND_ACK): 
    {
      // Handle ack
      break;
    }
    case (COMMAND_NACK): 
    {
      // Handle nack
      break;
    }
    case (COMMAND_ORNT_SET): 
    {
      // Handle orientation set
      // Response is ack
      byte resp_command = COMMAND_ACK;
      int args_length = LEN_ACKD_COMMAND;
      byte resp_args[args_length];
      resp_args[LOC_ACKD_COMMAND] = command;
      send_packet(resp_command, resp_args, args_length);
      break;
    }
    case (COMMAND_ORNT_READ): 
    {
      // Handle orientation read
      break;
    }
    case (COMMAND_ENV_READ): 
    {
      // Handle environmental read
      // Get data
      read_env();

      // Send response packet
      byte resp_command = COMMAND_ENV_RESPONSE;
      int args_length = LEN_TEMP + LEN_HUMID;
      byte resp_args[args_length];
      insert_double(resp_args, LOC_TEMP, temperature);
      insert_double(resp_args, LOC_HUMID, humidity);
      send_packet(resp_command, resp_args, args_length);
      break;
    }
    case (COMMAND_GPS_READ): 
    {
      // Handle GPS read
      // Get latitude
      double latitude = 100.56;

      // Get longitude
      double longitude = 300.24;

      // Send response packet
      byte resp_command = COMMAND_GPS_RESPONSE;
      int args_length = LEN_LAT + LEN_LONG;
      byte resp_args[args_length];
      insert_double(resp_args, LOC_LAT, latitude);
      insert_double(resp_args, LOC_LONG, longitude);
      send_packet(resp_command, resp_args, args_length);
      break;
    }
    case (COMMAND_EST_CONN):
    {
      // Respond with an Ack to establish the connection
      byte resp_command = COMMAND_ACK;
      int args_length = LEN_ACKD_COMMAND;
      byte resp_args[args_length];
      resp_args[LOC_ACKD_COMMAND] = command;
      send_packet(resp_command, resp_args, args_length);
    }
  default: 
    {
      // Invalid command received
      // Send nack
      byte resp_command = COMMAND_NACK;
      int args_length = LEN_NACKD_COMMAND;
      byte resp_args[args_length];
      resp_args[LOC_NACKD_COMMAND] = command;
      send_packet(resp_command, resp_args, args_length);
      break;
    }
  }

}  

//boolean has_header(byte *packet_bytes) {
//  return packet_bytes[LOC_HEADER] = HEADER;
//}
//
//// Send a packet with the given command and arguments over serial
//void send_packet(byte command, byte *args, int args_length) {
//  // Create byte array to send
//  byte packet_bytes[PACKET_LEN];
//
//  // Set all elements of array to zero
//  memset(packet_bytes, 0, sizeof(packet_bytes));
//
//  // Insert header and command
//  packet_bytes[LOC_HEADER] = HEADER;
//  packet_bytes[LOC_COMMAND] = command;
//
//  // Insert arguments
//  for (int i = 0; i < args_length; i++) {
//    packet_bytes[LOC_ARGS + i] = args[i];
//  }
//
//  // Write the packet to the serial port and then flush the output
//  Serial.write(packet_bytes, PACKET_LEN);
//  Serial.flush();
//}
//
//// Send the given byte array over serial
//void send_packet(byte *packet_bytes) {
//  Serial.write(packet_bytes, PACKET_LEN);
//  Serial.flush();
//}


// ======================= 
// HELPER FUNCTIONS
// =======================

// Insert the given double into the given byte array, starting at offset. It is inserted as a string.
void insert_double(byte *bytes, int offset, double value) {
  int width = 7; // 8 total bytes incl. 1 for null term
  int precision = 2;
  char value_str[width+1];
  dtostrf(value, width, precision, value_str);
  for (int i = 0; i < width+1; i++) {
    bytes[offset + i] = (byte) value_str[i];
  }
}

//void insert_int(byte *bytes, int offset, int value) {
//  for (int i = 0; i < 4; i++) {
//    bytes[offset + 3 - i] = (byte) (value >> (i * 8));
//  }
//}

// Insert the given short (2 bytes) into the given byte array, starting at offset.
void insert_short(byte *bytes, int offset, short int value) {
  for (int i = 0; i < 2; i++) {
    bytes[offset + 1 - i] = (byte) (value >> (i * 8));
  }
}


// ======================= 
// SENSOR FUNCTIONS
// =======================

void read_env() {
  int chk = DHT.read22(DHT22_PIN);
  switch (chk) {
  case DHTLIB_OK:
    temperature = DHT.temperature;
    humidity = DHT.humidity;
    break;
  case DHTLIB_ERROR_CHECKSUM:
    break;
  case DHTLIB_ERROR_TIMEOUT:
    break;
  default:
    break;
  }
}


// ======================= 
// HARDWARE FUNCTIONS
// =======================

void set_led(boolean on) {
  if (on) {
    digitalWrite(LED_PIN, HIGH);
  }
  else {
    digitalWrite(LED_PIN, LOW);
  }
}


