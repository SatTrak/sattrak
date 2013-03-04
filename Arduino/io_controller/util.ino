#include <stdlib.h>

const int D_WIDTH = 7; // Double strings are 8 total bytes incl. 1 for null term
const int D_PREC = 2;  // Double strings have 2 decimals places precision

// ======================= 
// HELPER FUNCTIONS
// =======================

// Insert the given double into the given byte array, starting at offset. It is inserted as a string.
void insert_double(byte *bytes, int offset, double value) {
  char value_str[D_WIDTH+1];
  dtostrf(value, D_WIDTH, D_PREC, value_str);
  for (int i = 0; i < D_WIDTH+1; i++) {
    bytes[offset + i] = (byte) value_str[i];
  }
}

// Extract a double from the given byte array. It should be represented as a string.
double extract_double(byte *bytes, int offset) {
  char *value_str;
  for (int i = 0; i < D_WIDTH+1; i++) {
    value_str[i] = (char) bytes[offset + i];
  }
  return strtod(value_str, NULL);
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

double radsToDegrees(double angleInRads) {
  return angleInRads * 180.0 / 3.1416;
}
