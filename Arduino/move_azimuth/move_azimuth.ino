#define MOTR_AZ_DIR_PIN 4
#define MOTR_AZ_PWM_PIN 5

void setup() {
  pinMode(MOTR_AZ_DIR_PIN, OUTPUT);
  pinMode(MOTR_AZ_PWM_PIN, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  int dir = waitForInput();

  if (dir == 0)
    digitalWrite(MOTR_AZ_DIR_PIN, HIGH);
  else
    digitalWrite(MOTR_AZ_DIR_PIN, LOW);

  analogWrite(MOTR_AZ_PWM_PIN, 80);

  delay(2000);

  analogWrite(MOTR_AZ_PWM_PIN, 0);
}

int waitForInput() {
  while(!Serial.available());
  int i = Serial.parseInt();
  while(Serial.read() != -1);
  return i;
}

