/*
 * Philip Diefenderfer
 * Inclinometer Test
 * (MuRata SCA61T-FA1H1G)
 */

// Init pins to inputs and outputs and prep SUART
void setup() {
  pinMode(13, OUTPUT);
  pinMode(12, INPUT);
  pinMode(11, OUTPUT);
  pinMode(10, OUTPUT);

  Serial.begin(9600);
}

// run loop to collect temp and incline data
void loop() {
  int temp = readTempData();
  float t = (temp - 197) / -1.083; // see datasheet for forumla
  Serial.print("Temp  data: ");
  Serial.println(t);
  delay(250);
  
  int incline = readInclineData();
  double inc = asin((incline - 1024.0)/819.0); // see datasheet for formula
  inc = inc * 180.0 / 3.1416; // I know this is a double, will convert later
  Serial.print("Incline data: ");
  Serial.println(inc);
  delay(250);
}

// read the incline data from the sensor
// done manually due to 11 bit data
int readInclineData(){
  digitalWrite(10, LOW); // select inclinometer
  
  int command = 0x10;    // read RDAX
  int recieved = 0;      // place to store incoming
  
  // loop to send command and tick the clock
  for(int i = 7; i >= 0; i--){
    digitalWrite(11, (command & (1 << i)));
    digitalWrite(13, HIGH);
    delay(1); // too high but good enough for now
    digitalWrite(13, LOW);
  }

  // loop to recieve data from inclinometer and tick clock
  for(int i = 10; i >= 0; i--){
    digitalWrite(13, HIGH);
    recieved |= (digitalRead(12) << i);
    digitalWrite(13, LOW);
    delay(1);
  } 
  
  digitalWrite(10, HIGH); // deselect inclinometer and reset connection
  return recieved;
}

// read the temp data from the sensor
// done manually due to copy/paste
int readTempData(){
  digitalWrite(10, LOW); // select the inclinometer
  int command = 0x08;    // read Temperature
  int recieved = 0;      // place to store incoming
  
  // loop to send command and tick the clock
  for(int i = 7; i >= 0; i--){
    digitalWrite(11, (command & (1 << i)));
    digitalWrite(13, HIGH);
    delay(1); // too high but good enough for now
    digitalWrite(13, LOW);
  }
  
  // loop to recieve data and tick the clock
  for(int i = 7; i >= 0; i--){
    digitalWrite(13, HIGH);
    recieved |= (digitalRead(12) << i);
    digitalWrite(13, LOW);
    delay(1);
  }
  
  digitalWrite(10, HIGH); // deselect the inclinometer
  return recieved;
}

