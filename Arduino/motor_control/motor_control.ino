/*
 * Philip Diefenderfer
 * Inclinometer Test
 * (MuRata SCA61T-FA1H1G)
 */

// Pins
#define MOTOR_PWM 3
#define CSB 10
#define MOSI 11
#define MISO 12
#define SCK 13

#define TARGET_INCLINE 0.0
#define TOLERANCE 5.0

float temp;
double incline;

// Init pins to inputs and outputs and prep SUART
void setup() {
  pinMode(CSB, OUTPUT);
  pinMode(MOSI, OUTPUT);
  pinMode(MISO, INPUT);
  pinMode(SCK, OUTPUT);
  pinMode(MOTOR_PWM, OUTPUT);

  Serial.begin(9600);
}

// run loop to collect temp and incline data
void loop() {
  incline = readInclineData();
  incline = incline * 180.0 / 3.1416; // I know this is a double, will convert later
  Serial.print("Incline: ");
  Serial.println(incline);
  
  // Adjust motors until inclinometer reads angle within TOLERANCE of TARGET_INCLINE
  if (abs(incline - TARGET_INCLINE) <= TOLERANCE) {
    analogWrite(MOTOR_PWN, 128);
  } else {
    analogWrite(MOTOR_PWM, 0);
  }
  
  delay(1);
}

// read the incline data from the sensor
// done manually due to 11 bit data
double readInclineData(){
  digitalWrite(CSB, LOW); // select inclinometer
  
  int command = 0x10;    // read RDAX
  int recieved = 0;      // place to store incoming
  
  // loop to send command and tick the clock
  for(int i = 7; i >= 0; i--){
    digitalWrite(MOSI, (command & (1 << i)));
    digitalWrite(SCK, HIGH);
    delayMicroseconds(1); // too high but good enough for now
    digitalWrite(SCK, LOW);
    delayMicroseconds(1);
  }

  // loop to recieve data from inclinometer and tick clock
  for(int i = 10; i >= 0; i--){
    digitalWrite(SCK, HIGH);
    delayMicroseconds(1);
    recieved |= (digitalRead(MISO) << i);
    digitalWrite(SCK, LOW);
    delayMicroseconds(1);
  } 
  
  digitalWrite(CSB, HIGH); // deselect inclinometer and reset connection
  return asin((received - 1024.0)/819.0); // see datasheet for formula
}

// read the temp data from the sensor
// done manually due to copy/paste
int readTempData(){
  digitalWrite(CSB, LOW); // select the inclinometer
  int command = 0x08;    // read Temperature
  int recieved = 0;      // place to store incoming
  
  // loop to send command and tick the clock
  for(int i = 7; i >= 0; i--){
    digitalWrite(MOSI, (command & (1 << i)));
    digitalWrite(SCK, HIGH);
    delayMicroseconds(1);
    digitalWrite(SCK, LOW);
    delayMicroseconds(1);
  }
  
  // loop to recieve data and tick the clock
  for(int i = 7; i >= 0; i--){
    digitalWrite(SCK, HIGH);
    delayMicroseconds(1);
    recieved |= (digitalRead(MISO) << i);
    digitalWrite(SCK, LOW);
    delayMicroseconds(1);
  }
  
  digitalWrite(CSB, HIGH); // deselect the inclinometer
  return recieved;
}

