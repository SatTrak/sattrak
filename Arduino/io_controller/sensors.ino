#include <dht.h>      // Temp/humidity library
#include <Wire.h>     // SPI library for compass reading
#include "TinyGPS.h"  // GPS library
#include <SoftwareSerial.h>  // Used for serial communication on pins other than 0 and 1

// ======================= 
// INSTANCE VARIABLES
// =======================

// Temp/humidity
dht DHT;
double temperature;
double humidity;

// GPS
TinyGPS gps;
SoftwareSerial nss(GPS_DATA_PIN, NULL_PIN);
float latitude;
float longitude;
unsigned long time; // ?

// Orientation
double azimuth;
double elevation;


// ======================= 
// SENSOR FUNCTIONS
// =======================

void sensors_init() {
  // Start software serial for GPS
  nss.begin(4800);
  
  // Start wire communication for compass sensor
  Wire.begin();

  // Set pin directions for inclinometer
  pinMode(INC_CSB_PIN, OUTPUT);
  pinMode(INC_MOSI_PIN, OUTPUT);
  pinMode(INC_MISO_PIN, INPUT);
  pinMode(INC_SCK_PIN, OUTPUT);
  
  // Initialize variables
  temperature = 0;
  humidity = 0;
  latitude = 0;
  longitude = 0;
  time = 0;
  azimuth = 0;
  elevation = 0;
}

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

void read_gps() {
  boolean newData = false;
  
  while (nss.available()) {
    if (gps.encode(nss.read()))
      newData = true;
  }
  
  if (newData) {
    gps.f_get_position(&latitude, &longitude, &time);
  }
}

void read_compass() {
  Wire.beginTransmission(HMC6352SlaveAddress);
  Wire.write(HMC6352ReadAddress);              // The "Get Data" command
  Wire.endTransmission();

  // Time delay required by HMC6352 upon receipt of the command: 6ms
  delay(6);

  // Get the two data bytes, MSB and LSB
  Wire.requestFrom(HMC6352SlaveAddress, 2); 

  //"The heading output data will be the value in tenths of degrees
  //from zero to 3599 and provided in binary format over the two bytes."
  byte MSB = Wire.read();
  byte LSB = Wire.read();

  float headingSum = (MSB << 8) + LSB;
  azimuth = headingSum / 10; 
}

void read_inclinometer() {
  // Select inclinometer
  digitalWrite(INC_CSB_PIN, LOW); 

  int received = 0;  // Place to store incoming digital signal

  // Loop to send command and tick the clock
  for(int i = 7; i >= 0; i--){
    digitalWrite(INC_MOSI_PIN, (INC_READ_RDAX & (1 << i)));
    digitalWrite(INC_SCK_PIN, HIGH);
    delayMicroseconds(1);
    digitalWrite(INC_SCK_PIN, LOW);
    delayMicroseconds(1);
  }

  // Loop to recieve data from inclinometer and tick clock
  for(int i = 10; i >= 0; i--){
    digitalWrite(INC_SCK_PIN, HIGH);
    delayMicroseconds(1);
    received |= (digitalRead(INC_MISO_PIN) << i);
    digitalWrite(INC_SCK_PIN, LOW);
    delayMicroseconds(1);
  } 

  // Deselect inclinometer and reset connection
  digitalWrite(INC_CSB_PIN, HIGH);
  
  // Convert digital signal to angle (see datasheet for formula)
  elevation = radsToDegrees(asin((received - INC_OFFSET)/INC_SENS));
}


