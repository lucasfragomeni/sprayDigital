boolean button1State;             // the current reading from the input pin
int lastButton1State = LOW;   // the previous reading from the input pin
long lastDebounce1Time = 0;  // the last time the output pin was toggled

boolean button2State;             // the current reading from the input pin
int lastButton2State = LOW;   // the previous reading from the input pin
long lastDebounce2Time = 0;  // the last time the output pin was toggled

boolean button3State;             // the current reading from the input pin
int lastButton3State = LOW;   // the previous reading from the input pin
long lastDebounce3Time = 0;  // the last time the output pin was toggled

const long DEBOUNCE_DELAY = 50;    // the debounce time; increase if the output flickers

void setup() {
  pinMode(2, INPUT);
  pinMode(4, INPUT);
  pinMode(7, INPUT);
  Serial.begin(9600);
}

void loop() {
  int reading = digitalRead(2);
  if (reading != lastButton1State) {
    lastDebounce1Time = millis();
  } 
  if ((millis() - lastDebounce1Time) > DEBOUNCE_DELAY) {
    if(lastButton1State != button1State) {
      button1State = !button1State;
      if(!button1State) {
        Serial.print("b:");
        Serial.println("1");
      }
    }
  }  
  lastButton1State = reading;

  reading = digitalRead(4);
  if (reading != lastButton2State) {
    lastDebounce2Time = millis();
  } 
  if ((millis() - lastDebounce2Time) > DEBOUNCE_DELAY) {
    if(lastButton2State != button2State) {
      button2State = !button2State;
      if(!button2State) {
        Serial.print("b:");
        Serial.println("2");
      }
    }
  }
  lastButton2State = reading;  

  reading = digitalRead(7);
  if (reading != lastButton3State) {
    lastDebounce3Time = millis();
  } 
  if ((millis() - lastDebounce3Time) > DEBOUNCE_DELAY) {
    if(lastButton3State != button3State) {
      button3State = !button3State;
      if(!button3State) {
        Serial.print("b:");
        Serial.println("3");
      }
    }
  }
  lastButton3State = reading;  
}
