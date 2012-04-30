#include <TimerOne.h>

#define M_E 2.7182818284590452353602874713526624977572470936999595749669

const byte INIT = 1;
const byte ACK = 2;

const int LED_INIT = 13;
const int LED_ACK = 12;

const long DELAY_TIMEOUT = 30000;
const int  ACK_BLINKS = 3;

// INICIALIZACAO E ACKNOWLEDGMENT
boolean inicializado = false;
long timestampTimeout;

// DISTÂNCIA
const int IR_SENSOR_PIN = A1;
int distanciaAnterior = -1;

// COR
const int COR_PIN = A0;
const int METADE_SELETOR_COR = 150;
int corAnterior = -1;

const long DELAY_COR = 2000;
const long MUDANCA_MIN_COR = 35;
long timestampUltimaAtividadeCor = 0;
int leituraAnteriorCor = -100;

byte incomingByte;
int ackBlinks = 0;

long timestamp;

/*
 *
 */
void setup() {
  Serial.begin(9600);

  pinMode(LED_INIT, OUTPUT);
  pinMode(LED_ACK, OUTPUT);

  //Inicializa o contador num intervalo de 250ms
  Timer1.initialize(150000);
  //Associa o contador à função de piscar o Led:
  Timer1.attachInterrupt(controlarLedsSinalizadores);
}

/*
 *
 */
void loop() {
  //Inicializacao e Acknowledgment
  if(Serial.available()) {
    incomingByte = Serial.read();
    
    //Init
    if(incomingByte == INIT) {
      inicializado = true;
      timestampTimeout = millis();
    }
    //Ack
    else if(incomingByte == ACK) {
      ackBlinks = 0;
      inicializado = true;
      timestampTimeout = millis();
    }
  }
  
  //if(inicializado) {
    //Cor
    int cor = lerCor();
    if(cor != corAnterior) {
      Serial.print("c:");
      Serial.println(cor);
      corAnterior = cor;
    }
    else {
      if(millis() - timestamp >= 50) {
        //Distancia
        int distancia = lerDistancia();
        if((distancia != distanciaAnterior && abs(distancia - distanciaAnterior) > 1)) {
          Serial.println((int) distConv(distancia));
          distanciaAnterior = distancia;
        }
        timestamp = millis();
      }
    }
  //}

  checkTimeout();
  
  delay(25);
}

void checkTimeout() {
  if(inicializado) {
    if((millis() - timestampTimeout) > DELAY_TIMEOUT) {
      inicializado = false;
    }
  }
}

/*
 * distConv()
 *
 * convert voltage reading to millimeters.
 *
 * exponential fit from data set:
 *
 * --------------------------------------------
 * reading   | distance | reading  | distance 
 * --------------------------------------------
 * 6         | 3        | 80       | 25
 * 26        | 5        | 84       | 30
 * 57        | 10       | 85       | 35
 * 70        | 15       | 87       | 40
 * 77        | 20       |          |
 * --------------------------------------------
 *
 * best fit: 3.96929 e^(0.0523051 x)
 *
 */

double distConv(double x) {
  return 3.96929 * pow(M_E, 0.0523051 * x);
}

void controlarLedsSinalizadores() {
  if(!inicializado) {
    digitalWrite(LED_INIT, !digitalRead(LED_INIT));
  } else {
    digitalWrite(LED_INIT, HIGH);
  }

  //Sinalizacao do ACK
  if(ackBlinks < (ACK_BLINKS * 2)) {
    digitalWrite(LED_ACK, !digitalRead(LED_ACK));
    ackBlinks++;
  } else {
    digitalWrite(LED_ACK, LOW);
  }    
}

/*
 * Verifica se houve atividade no seletor de cor apos um periodo sem mudar a faixa de valores
 */
boolean verificaAtividadeSeletorCor() {
  boolean retorno = false;

  //Le o valor do seletor de cor
  int amostragemCor[7];
  for(int i = 0; i < (sizeof(amostragemCor)/sizeof(int)); i++) {
    amostragemCor[i] = analogRead(COR_PIN);
  }
  int leituraCor = moda(amostragemCor, sizeof(amostragemCor)/sizeof(int));
  
  //Se a diferenca para o valor anterior for maior 
  //que 50 pontos, considera como atividade
  if(abs(leituraAnteriorCor - leituraCor) > MUDANCA_MIN_COR) {
    
    //Se o instante da ultima mudanca no seletor tiver sido
    //a mais de 1000ms, considera que teve atividade recente.
    if(millis() - timestampUltimaAtividadeCor > DELAY_COR) {
      retorno = true;
    }

    timestampUltimaAtividadeCor = millis();
    leituraAnteriorCor = leituraCor;
  }
  
  return retorno;
}

int lerCor() {
  int amostragemCor[7];
  for(int i = 0; i < (sizeof(amostragemCor)/sizeof(int)); i++) {
    amostragemCor[i] = analogRead(COR_PIN);
  }
  int leituraCor = moda(amostragemCor, sizeof(amostragemCor)/sizeof(int));

  /*
   * WARNING: __Golden__ code bellow
   *
   * Those were __logarithmic__ potentiometers, in which teh difference between
   * two angles would be directly proportional to teh difference between
   * exp(leituraCor).
   *
   */
#if 0
  //Os potenciometros que comprei tem um erro que
  //ate a metade do seletor, retorna 2/10 do maximo
  if(leituraCor <= METADE_SELETOR_COR) {
    return map(leituraCor, 0, METADE_SELETOR_COR, 0, 7);
  } else {
    return map(leituraCor, METADE_SELETOR_COR, 1023, 7, 15);
  }
#endif

  /* New __linear__ potentiometer */
  return map(leituraCor, 0, 1023, 0, 15);
}

int lerDistancia() {
  int amostragemDistancia[7];
  for(int i = 0; i < (sizeof(amostragemDistancia)/sizeof(int)); i++) {
//    //Sonar
//    amostragemDistancia[i] = analogRead(SONAR_PIN);

    //IR DIST SENS.
    amostragemDistancia[i] = map(analogRead(IR_SENSOR_PIN), 650, 100, 5, 80);
  }
  
//  //Sonar
//  return media(amostragemDistancia, sizeof(amostragemDistancia)/sizeof(int));

//IR
  return moda(amostragemDistancia, sizeof(amostragemDistancia)/sizeof(int));
}

/**
 * Calcula a média
 */
int media(int amostragem[], int length) {
  int media;
  for(int i = 0; i < length; i++) {
    media += amostragem[i];
  }
  
  return (media / length);
}

/**
 * Calcula a moda
 */
int moda(int amostragem[], int length) {
  int contagem = 0;
  int moda = 0;
  int maior = 0;
  for(int a = 0; a < length; a++) {
    contagem = 0;
    for(int b = a+1; b < length; b++) {
      if(amostragem[a] == amostragem[b]) {
        contagem++;
      }
    }
    if(contagem > maior) {
      moda = amostragem[a];
      maior = contagem;
    }
  }

  return moda;
}

