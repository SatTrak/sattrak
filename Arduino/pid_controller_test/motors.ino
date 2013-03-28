#include <PID_v1.h>

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
  set_azimuth(az);
  set_elevation(el);
}

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
    else if (err > 2  && err < 358){
      analogWrite(MOTR_AZ_PWM_PIN, 85);
    } 
    else {
      digitalWrite(MOTR_AZ_PWM_PIN, LOW);
      return; 
    }

    delay(50); // arbitrary choice
  } 
}

// Set the azimuth to the given target angle
void set_azimuth_pid(double target) {
  double heading;
  double motor_pwm;


}

// Set the elevation to the given target angle
void set_elevation(double target) {
  while (1) {
    double incline = read_inclinometer();
    double err = incline - target;
    Serial.print("Elevation = ");
    Serial.print(incline);
    Serial.print(", Error ");
    Serial.println(err);

    // Set elevation motor direction based on sign of error
    if (err > 0){
      digitalWrite(MOTR_EL_DIR_PIN, HIGH);
    } 
    else {
      digitalWrite(MOTR_EL_DIR_PIN, LOW);
    }

    // Step down the PWM to the motor driver at various thresholds until the error is 0
    double err_mag = abs(err);
    if (err_mag < 1) {
      analogWrite(MOTR_EL_PWM_PIN, 0);
      return;
    } 
    else if (err_mag < 10) {
      analogWrite(MOTR_EL_PWM_PIN, 60);
    } 
    else if (err_mag < 20) {
      analogWrite(MOTR_EL_PWM_PIN, 80);
    } 
    else {
      analogWrite(MOTR_EL_PWM_PIN, 120);
    }

    delay(1);
  }
}

// Set the elevation to the given target angle
void set_elevation_pid(double target) {
  double kp=2, ki=0, kd=0;

  double incline = read_inclinometer();
  double motor_pwm = 80;

  PID elev_pid(&incline, &motor_pwm, &target, kp, ki, kd, DIRECT);

  //Setup the pid 
  elev_pid.SetMode(AUTOMATIC);

  while (1) {
    incline = read_inclinometer();
    elev_pid.Compute();
    
    Serial.print("Elevation = ");
    Serial.print(incline);
    Serial.print(", \t\tPWM ");
    Serial.println(motor_pwm);
    
    if ((incline - target) > 0){
      digitalWrite(MOTR_EL_DIR_PIN, HIGH);
    } 
    else {
      digitalWrite(MOTR_EL_DIR_PIN, LOW);
    }
    
    analogWrite(MOTR_EL_PWM_PIN, motor_pwm);
  }
}

// Level the mount by setting elevation to 0
void level() {
  set_elevation(0);
}













