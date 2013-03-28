#include "pins.h"         // Pin numbers
#include "sensors.h"      // Sensor constant definitions

// ======================= 
// STANDARD FUNCTIONS
// =======================

void setup() {
  // Initialization
  Serial.begin(9600);
  sensors_init();
  motors_init();
  
  delay(100);
}

void loop() {
  Serial.println("Enter elevation: ");
  while(!Serial.available());
  int target_el = Serial.parseInt();
  while(Serial.read() != -1);
  set_elevation_pid((double) target_el);
}
