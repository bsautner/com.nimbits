#include "Arduino.h"
#include <Ethernet.h>
#include <SPI.h>
#include <Nimbits.h>

byte mac[] = { 
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "192.168.1.42";
Nimbits client;
char email[] = "support@nimbits.com";
char apiKey[] = "KEY";
char clientId[] = "some_unique_string3";
int port = 8080;
char* points[] = {
  "foo", "bar"};

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

}
 



