#include "Arduino.h"
#include <Ethernet.h>
#include <SPI.h>
#include <WebSocketClient.h>

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "192.168.1.42";
WebSocketClient client;
char email[] = "support@nimbits.com";
char apiKey[] = "KEY";
char clientId[] = "some_unique_string";
int port = 8080;
char* points[] = {"foo", "bar"};
  

void setup() {
  Serial.begin(9600);
  
  Ethernet.begin(mac);
  client.connect(server, email, apiKey, points , port, clientId);
  client.setDataArrivedDelegate(dataArrived);
  client.send("Hello World!");
}

void loop() {
  client.monitor();
}

void dataArrived(WebSocketClient client, String data) {
  Serial.println("Data Arrived: " + data);
}
