/**

How to push values to a nimbits cloud using a read/write id instead of a password.
http://nimbits.com/howto_security.jsp

Usage: login to your nimbits cloud or the public cloud: http://cloud.nimbits.com

Also, remember to import the ArduinoJson library: https://github.com/bblanchon/ArduinoJson

**/
#include "Arduino.h"
#include <Ethernet.h>
#include <SPI.h>
#include <Nimbits.h>
#include <ArduinoJson.h>


byte mac[] = { 
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "cloud.nimbits.com";
char email[] = "test@example.com";

//This can be your password if you created an account and set a password, or an access id if you
//logged into the server and created a id - use an access id if you log into nimbits with a google account, for example.
char password[] ="id";
int port = 80;

String point = "pointname";
  
Nimbits client(server, port);

void setup() {
  
  Serial.begin(9600);
  Ethernet.begin(mac);

  Serial.println("Logging in...");
  String authToken = client.login(email, password);
  
  Serial.println("Ready");
}

void loop() {
 
    client.recordValue(random(300), point); 
    delay(1000);
}

