#include <stdlib.h>

// ======================= 
// COMMUNICATION FUNCTIONS
// =======================

boolean has_header(byte *packet_bytes) {
  return packet_bytes[LOC_HEADER] = HEADER;
}

// Send a packet with the given command and arguments over serial
void send_packet(byte command, byte *args, int args_length) {
  // Create byte array to send
  byte packet_bytes[PACKET_LEN];

  // Set all elements of array to zero
  memset(packet_bytes, 0, sizeof(packet_bytes));

  // Insert header and command
  packet_bytes[LOC_HEADER] = HEADER;
  packet_bytes[LOC_COMMAND] = command;

  // Insert arguments
  for (int i = 0; i < args_length; i++) {
    packet_bytes[LOC_ARGS + i] = args[i];
  }

  // Write the packet to the serial port and then flush the output
  Serial.write(packet_bytes, PACKET_LEN);
  Serial.flush();
}

// Send the given byte array over serial
void send_packet(byte *packet_bytes) {
  Serial.write(packet_bytes, PACKET_LEN);
  Serial.flush();
}
