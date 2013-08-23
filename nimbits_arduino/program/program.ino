 
#include "Nimbits.h"
#include <Client.h>
#include <Ethernet.h>

#include <SPI.h>
#include <PString.h>
#include <stdlib.h>

#define MAX_KEY_SIZE 64
#define CATEGORY 2
#define POINT 1
#define SUBSCRIPTION 5
 
 
//nimbits settings, set the instance name (nimbits-02 is the public cloud on https://cloud.nimbits.com) the email of the account owner, and a read write key they have created.
String instance = "nimbits-02";
char owner[] = "bsautner@gmail.com";
String readWriteKey = "key";
long randNumber;
byte mac[] = {0x90, 0xA2, 0xDA, 0x00, 0x54, 0x39}; //this ethernet shield's MAC address

//the names of the entities that will either be created or queried on startup
char folder[] = "RD";
char point[] = "RD Alerting Point";

Nimbits nimbits(instance, owner, readWriteKey);

void setup() {
  Serial.begin(9600); //initialize serial communication for debugging
  if (Ethernet.begin(mac) == 0) {
    Serial.println("DHCP Failed!");
    while(true);
  }
  randomSeed(analogRead(0));
  Serial.println("Online"); 
  delay(1000);
}

void loop() { 
    delay(1000);
    nimbits.recordValue(random(300),"","bsautner@gmail.com/lab_temp"); 
}

 





 


