#include "Arduino.h"
#include <Ethernet.h>
#include <SPI.h>
#include <Nimbits.h>

/**
* watched a nimbits data point called switch1 and turns someone on or off based on the value. 
* see the switch tutorial on nimbits.com
**/

byte mac[] = { 
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "192.168.1.14";
Nimbits client;
char email[] = "support@nimbits.com";
char apiKey[] = "KEY";
char clientId[] = "some_unique_string";
int port = 8080;
char* points[] = {"switch1"};

void setup() {
  Serial.begin(9600);

  Ethernet.begin(mac);
  client.connect(server, email, apiKey, points , port, clientId);
  client.setDataArrivedDelegate(incoming);
  client.send("Hello Nimbits!");
}

void loop() {
  client.monitor();


}


//data will arrive in this format: pointname,value

void incoming(Nimbits client, String point, float value) {
  Serial.println("Data Arrived: " + point);
  Serial.println(value);
  if (value == 1.0) {
    Serial.println("Turn something On!");
    
  }
  else {
     Serial.println("Turn something Off!");
    
  }

}
 



