 
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
char owner[] = "test@gmail.com";
String readWriteKey = "nimbits key";
 
byte mac[] = {
  0x90, 0xA2, 0xDA, 0x00, 0x54, 0x39}; //this ethernet shield's MAC address
 
  
 
//buffers for holding the ids of our entities provided by the server.
char pointId[MAX_KEY_SIZE];
char folderId[MAX_KEY_SIZE]; 

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

  Serial.println("Online"); 
  delay(1000);

}

void loop() {
   

    postData();
    delay(1000);
  }

 

}


void postData(){  
 
  //  
  //  post.begin();
  //  post.print("GET /service/currentvalue?&point=Pressure&email=test@gmail.com&value=");
  //  post.print(dtostrf(P, 5, 3, buffer));
  //  post.println("&secret=95b73139-2b0b-4e9a-89a5-23275955bf9e HTTP/1.1");
  //  post.println("Host:nimbits1.appspot.com");
  //  post.println("Accept-Language:en-us,en;q=0.5");
  //  post.println("Accept-Encoding:gzip,deflate");
  //  post.println("Connection:close");
  //  post.println("Cache-Control:max-age=0");
  //  post.println();
  //  
  //  client.println(post);
  //  
  //  client.flush();
  //  client.stop();
}

 


