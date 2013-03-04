#include "pins.h"         // Pin numbers
#include "serial_comm.h"  // Serial protocol definitions
#include "sensors.h"      // Sensor constant definitions

// ======================= 
// STANDARD FUNCTIONS
// =======================

void setup() {
  // Initialization
  serial_init();
  sensors_init();
  motors_init();
  
  delay(100);
}

void loop() {
  recv_and_handle_packet();
}
