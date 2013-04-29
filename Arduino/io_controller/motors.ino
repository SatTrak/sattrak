#define AVERAGES 15

// ======================= 
// MOTOR FUNCTIONS
// =======================

void motors_init() {
  pinMode(MOTR_AZ_DIR_PIN, OUTPUT);
  pinMode(MOTR_AZ_PWM_PIN, OUTPUT);
  pinMode(MOTR_EL_DIR_PIN, OUTPUT);
  pinMode(MOTR_EL_PWM_PIN, OUTPUT);
}

// Set the orientation of the mount to the given azimuth and elevation angles
void set_orientation(double az, double el) {
  // Ignore invalid azimuth angles
  if (az > 359 || az < 0 || el > 45 || el < -45) {
    return;
  }

  level();
  delay(500);
  //set_azimuth(az);
  //set_elevation(el);
  orientMount(az);
  levelPulse(el);
}

// ==========
// Elevation
// ==========

// Set the azimuth to the given target angle
void set_azimuth(double target) {
  while(1){
    double heading = read_compass();
    double err = heading - target;
    if (err < 0) {
      err = err + 360;
    }

    if (err > 180){ // split the difference for the middle
      digitalWrite(MOTR_AZ_DIR_PIN, HIGH);
    } 
    else {
      digitalWrite(MOTR_AZ_DIR_PIN, LOW);
    } 

    if (err > 90 && err < 270){
      analogWrite(MOTR_AZ_PWM_PIN, 120); 
    }
    else if (err > 45 && err < 315){
      analogWrite(MOTR_AZ_PWM_PIN, 100);   
    }
    else if (err > 20  && err < 340){
      analogWrite(MOTR_AZ_PWM_PIN, 85);
    } 
    else if (err > 0.5 && err < 359.5){
      //digitalWrite(MOTR_AZ_PWM_PIN, LOW);
      pulse_azimuth();
    } 
    else {
      digitalWrite(MOTR_AZ_PWM_PIN, LOW);
      return;
    }

    delay(50); // arbitrary choice
  } 
}

void pulse_azimuth() {
  digitalWrite(MOTR_AZ_PWM_PIN, HIGH);
  delay(8);
  digitalWrite(MOTR_AZ_PWM_PIN, LOW);
  delay(50);
}

void orientMount(double angle) {
  while(1){

    double heading = read_compass();
    double difference = heading - angle;

    // if the heading and desired angle are on separate sides of 180 degrees
    if((heading > 180) == (angle < 180)) {
      if (signbit(difference)){ // split the difference for the middle 
        digitalWrite(MOTR_AZ_DIR_PIN, LOW);
      } 
      else {
        digitalWrite(MOTR_AZ_DIR_PIN, HIGH);
      } 
    } 
    else {
      if (signbit(difference)){ // split the difference for the middle 
        digitalWrite(MOTR_AZ_DIR_PIN, HIGH);
      } 
      else {
        digitalWrite(MOTR_AZ_DIR_PIN, LOW);
      }  
    }

    // recharacterize these PWM values <=======================================================
    if (difference < 0) {          // if less than 0, add 3600 to match
      difference += 360;
    }

    if (difference > 90 && difference < 270){
      analogWrite(MOTR_AZ_PWM_PIN, 120); 
    }
    else if (difference > 45 && difference < 315){
      analogWrite(MOTR_AZ_PWM_PIN, 110);   
    }
    else if (difference > 20  && difference < 340){
      analogWrite(MOTR_AZ_PWM_PIN, 100);
    } 
    else {
      digitalWrite(MOTR_AZ_PWM_PIN, LOW);
      heading = orientPulse(angle);
      return; 
    }
    //    delay(50); // arbitrary choice
  }
}

// parameter is decimal fixed point of one decimal digit
int orientPulse(double angle) {
  delay(1000);  // wait for settle

  double loc = read_compass();
  double diff = loc - angle;
  boolean dir = signbit(diff);

    while ((abs(diff) > 1 && abs(diff) < 359) && !(dir ^ signbit(diff))) {
      if(angle == 0 && loc > 180){
      digitalWrite(MOTR_AZ_DIR_PIN, HIGH);      
    }
    else if (angle == 0 && loc < 180) {
      digitalWrite(MOTR_AZ_DIR_PIN, LOW);
    } 
    else {

      if (signbit(diff)){
        digitalWrite(MOTR_AZ_DIR_PIN, HIGH);
      }
      else {
        digitalWrite(MOTR_AZ_DIR_PIN, LOW);
      }
    }
    // the pulsing works
    digitalWrite(MOTR_AZ_PWM_PIN, HIGH);
    delay(8);

    digitalWrite(MOTR_AZ_PWM_PIN, LOW);
    delay(50);

    loc = read_compass();
    diff = loc - angle;
  }
  return read_compass();
}

// ==========
// Elevation
// ==========

// Set the elevation to the given target angle
void set_elevation(double target) {
  while (1) {
    double incline = read_inclinometer();
    double err = incline - target;

    // Set elevation motor direction based on sign of error
    if (err > 0){
      digitalWrite(MOTR_EL_DIR_PIN, HIGH);
    } 
    else {
      digitalWrite(MOTR_EL_DIR_PIN, LOW);
    }

    // Step down the PWM to the motor driver at various thresholds until the error is 0
    double err_mag = abs(err);
    if (err_mag > 20) {
      analogWrite(MOTR_EL_PWM_PIN, 120);
    }
    else if (err_mag > 10) {
      analogWrite(MOTR_EL_PWM_PIN, 80);
    }
    else if (err_mag > 1) {
      analogWrite(MOTR_EL_PWM_PIN, 60);
    }
    else {
      analogWrite(MOTR_EL_PWM_PIN, 0);
      //pulse_elevation(target);
      return;
    } 

    delay(1);
  }
}

double levelPulse(double angle) {
  delay(1000);  // wait for settle

  volatile double in = read_inclinometer();

  while(abs(in - angle) > 1) {

    double i = in - angle;

    if (i > 0){
      digitalWrite(MOTR_EL_DIR_PIN, HIGH);
    }
    else {
      digitalWrite(MOTR_EL_DIR_PIN, LOW);
    }

    digitalWrite(MOTR_EL_PWM_PIN, HIGH);
    delay(5);

    digitalWrite(MOTR_EL_PWM_PIN, LOW);
    delay(50);

    in = read_inclinometer();
  }

  return in;
}

// Level the mount by setting elevation to 0
void level() {
  levelPulse(0);
  //set_elevation(0);
}












