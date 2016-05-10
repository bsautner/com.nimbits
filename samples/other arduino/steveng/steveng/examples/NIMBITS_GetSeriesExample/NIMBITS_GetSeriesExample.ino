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
#include "Nimbits2.h"

  //nimbits settings, set the instance name (nimbits-02 is the public cloud on https://cloud.nimbits.com) the email of the account owner, and a read write id they have created.
    String instance = "nimbits-02";
    char owner[] = "YourGmailAddress";
    String readWriteKey = "YourNimbitsKey";
    byte mac[] = {0x90, 0xA2, 0xDA, 0x00, 0x54, 0x39}; //this ethernet shield's MAC address

    Nimbits nimbits(instance, owner, readWriteKey);
    struct NimSeries Nim;
    
void setup() {
    int errcount=0;
    
    Serial.begin(9600); //initialize serial communication for debugging
    Serial.println("In setup");
    while (Ethernet.begin(mac) == 0){
      errcount+=1;
      Serial.println("DHCP Failed!");
      if (errcount>5){
        Serial.print("DHCP Final Failure. Stopping....");
        while(true);
      }
    }
           
     Serial.println("Online");
}

void loop() {

  int count=9;  // number of items requested
  int ret_count; // number of items received
  int i;
  float value;
    
  Serial.println("In loop");
  
  ret_count= nimbits.getSeries(count, "YourGmailAddress/YourPointName", Nim);
  
  Serial.println(Nim.DataHeader);
  
  for(i=0;i<ret_count;i++){
    Serial.print(Nim.Year[i]);
    Serial.print("/");
    Serial.print(Nim.Month[i]);
    Serial.print("/");
    Serial.print(Nim.Day[i]);
    Serial.print("    ");
    Serial.print(Nim.Hour[i]);
    Serial.print(":");
    Serial.print(Nim.Minute[i]);
    Serial.print(":");
    Serial.print(Nim.Second[i]);
    Serial.print("    ");
    Serial.println(Nim.Data[i]);
    value=atof(Nim.Data[i]);  // convert to float and do some more stuff
    
  }
  
  while(true);  // stop once the series data is printed 
}


