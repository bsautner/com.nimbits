#include <Arduino.h>
#include <Dhcp.h>
#include <Dns.h>
#include <EthernetServer.h>
#include <util.h>
#include <EthernetUdp.h>
#include <Ethernet.h>
#include <EthernetClient.h>
#include <SPI.h>
#include <stdlib.h>
#include <stdio.h>
#include "Nimbits.h"
#define BUFFER_SIZE 1024
#define PORT 80
#define CATEGORY 2
#define POINT 1
#define SUBSCRIPTION 5

const char *GOOGLE = "google.com";


const String CLIENT_TYPE_PARAM="&client=arduino";
const String APP_SPOT_DOMAIN = ".appspot.com";
const String PROTOCAL = "HTTP/1.1";
const int WAIT_TIME = 1000;
const char quote = '\"';


String _ownerEmail;
String _instance;
String _accessKey;
Nimbits::Nimbits(String instance, String ownerEmail, String accessKey){
  _instance = instance;
  _ownerEmail = ownerEmail;
  _accessKey = accessKey;

}


String createSimpleJason(char *name, char *parent, int entityType) {


}
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
void Nimbits::recordValue(double value, String note, char *pointId) {
  EthernetClient client;

  String json;
  json =  "{\"d\":\"";
  json +=floatToString(value, 4);

  json += "\",\"n\":\""; 
  json +=  note; 
  json +=  "\"}"; 
  String content;
  content = "email=";

  content += _ownerEmail;
  content += "&key=";
  content += _accessKey;
  content += "&json=";
  content += json;
  content += "&id=";
  content +=  pointId;

  Serial.println(content);

  if (client.connect(GOOGLE, PORT)) {
    client.println("POST /service/v2/value HTTP/1.1");
    client.println("Host:nimbits-02.appspot.com");
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

void Nimbits::addEntityIfMissing(char *key, char *name, char *parent, int entityType, char *settings) {
  EthernetClient client;
  Serial.println("adding");
  String retStr;
  char c;
  // String json;
  String json;
  json =  "{\"name\":\"";
  json += name; 
  json.concat("\",\"description\":\""); 
  json +=   "na"; 
  json += "\",\"entityType\":\""; 
  json +=  String(entityType); 
  json +=  "\",\"parent\":\""; 
  json +=   parent; 
  json += "\",\"owner\":\""; 
  json +=  _ownerEmail;
  json +=  String("\",\"protectionLevel\":\"");
  json +=   "2";
  
  //return json;
  switch (entityType) {
  case 1: 
    // json = createSimpleJason(name, parent, entityType); 

    break;
  case 2: 
    // json = createSimpleJason(name, parent, entityType); 
    break;
  case 5: 
    json +=  "\",\"subscribedEntity\":\""; 
    json +=   parent; 
    json +=  "\",\"notifyMethod\":\""; 
    json +=   "0"; 
    json +=  "\",\"subscriptionType\":\""; 
    json +=   "5"; 
    json +=  "\",\"maxRepeat\":\""; 
    json +=   "30"; 
    json +=  "\",\"enabled\":\""; 
    json +=   "true"; 

    break;
  }
  json += settings;
  json +=  "\"}";
  Serial.println(json);
  String content;
  content = "email=";

  content += _ownerEmail;
  content += "&key=";
  content += _accessKey;
  content += "&json=";
  content += json;
  content += "&action=";
  content += "createmissing";
  Serial.println(content);
  if (client.connect(GOOGLE, PORT)) {
    client.println("POST /service/v2/entity HTTP/1.1");
    client.println("Host:nimbits-02.appspot.com");
    client.println("Connection:close");
    client.println("User-Agent: Arduino/1.0");
    client.println("Cache-Control:max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(content.length());
    client.println();
    client.println(content);

    while(client.connected() && !client.available()) delay(1);
    int contentLength = 0;
    char buffer[BUFFER_SIZE];


    while (client.available() && contentLength++ < BUFFER_SIZE) {
      c = client.read();
      Serial.print(c);
      buffer[contentLength] = c;
    }
    Serial.println("getKeyFromJson");
    Serial.println(sizeof(buffer));
    int i=0;
    char item[] = {
      "\"key\":"          };
    while (i++ < sizeof(buffer) - sizeof(item)) {
      boolean found = false;
      found = false;
      for (int v = 0; v < sizeof(item) -1; v++) {

        if (buffer[i+v] != item[v]) { 
          found = false;
          break;
        }
        found = true;
      }
      if (found) {
        break;
      }


    }

    i = i + sizeof(item)-1;
    int keySize = 0;
    while (i++ < sizeof(buffer)-1) {
      if (buffer[i] == quote) {
        break;
      }
      else {
        key[keySize++] = buffer[i];
      }
    }
    key[keySize] = '\0';
    Serial.println(key);



    client.stop();
  }
  else {
    Serial.println("could not connect");

  }
  delay(1000);
}


//String Nimbits::getTime() {
//  EthernetClient client;

// if (client.connect(GOOGLE, PORT)) {
//  client.print("GET /service/v2/time?");
//writeAuthParamsToClient(client);
//writeHostToClient(client);

// return getResponse(client);


//}




//record a value

//record data

//create point

//create point with parent

//batch update

//delete point

//get children with values





























