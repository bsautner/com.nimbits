 
#include <Client.h>
#include <PString.h>
#include <stdlib.h>
#include <Arduino.h>
#include <Dhcp.h>
#include <Dns.h>
#include <EthernetServer.h>
#include <util.h>
#include <EthernetUdp.h>
#include <Ethernet.h>
#include <EthernetClient.h>
#include <SPI.h>
#include <stdio.h>


 
 
//nimbits settings, set the instance name (nimbits-02 is the public cloud on https://cloud.nimbits.com) the email of the account owner, and a read write key they have created.
//or if posting to your server, 192.168.1.100:8080/nimbits for example.
#define PORT 80
String instance = "nimbits-02"; 
String owner = "test@example.com";
String readWriteKey = "key";
const char *URL = "nimbits-02.appspot.com";

long randNumber;
byte mac[] = {0x90, 0xA2, 0xDA, 0x00, 0x54, 0x39}; //this ethernet shield's MAC address

 
 
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
    recordValue(random(300),"foo"); 
}

/**
post your new value
create the required json and posts to the http value service
*/

void recordValue(double value, char *pointId) {
  EthernetClient client;

  String json;
  json =  "{\"d\":\"";
  json +=floatToString(value, 4);
  json +=  "\"}"; 
  String content;
  content = "email=";

  content += owner;
  content += "&key=";
  content += readWriteKey;
  content += "&json=";
  content += json;
  content += "&id=";
  content +=  pointId;

  Serial.println(content);

  if (client.connect(URL, PORT)) {
    client.println("POST /service/v2/value HTTP/1.1");
    client.println("Connection:close");
    client.println("User-Agent: Arduino/1.0");
    client.println("Cache-Control:max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(content.length());
    client.println();
    client.println(content);

    while(client.connected() && !client.available()) delay(1);
    while (client.available() ) {
      char c = client.read();
      Serial.print(c);

    }
  }

}


//helper method to convert a double to a string for doing an http post
String floatToString(double number, uint8_t digits) 
{ 
  String resultString = "";
  // Handle negative numbers
  if (number < 0.0)
  {
    resultString += "-";
    number = -number;
  }

  // Round correctly so that print(1.999, 2) prints as "2.00"
  double rounding = 0.5;
  for (uint8_t i=0; i<digits; ++i)
    rounding /= 10.0;

  number += rounding;

  // Extract the integer part of the number and print it
  unsigned long int_part = (unsigned long)number;
  double remainder = number - (double)int_part;
  resultString += int_part;

  // Print the decimal point, but only if there are digits beyond
  if (digits > 0)
    resultString += "."; 

  // Extract digits from the remainder one at a time
  while (digits-- > 0)
  {
    remainder *= 10.0;
    int toPrint = int(remainder);
    resultString += toPrint;
    remainder -= toPrint; 
  } 
  return resultString;
}

 


