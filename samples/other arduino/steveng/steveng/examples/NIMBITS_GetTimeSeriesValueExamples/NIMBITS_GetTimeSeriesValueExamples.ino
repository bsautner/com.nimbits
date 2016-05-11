//  These sinmple examples will demonstrate how to easily set up the parameters so your
// Arduino can get information from the Nimbits Cloud.
//  GetTime is especially useful to test if you have connectivity to Nimbits as no user ID is needed.
//  This example was written by Steven Guterman and is in the public domain. Please include this header
//  in all subsequent versions. November 28, 2013

//  To use this example you will need to add your Nimbits ID, Nimbits id, and point ID (total of 4 changes).
//  Depending on your ethernet board, you may need to replace the MAC address with your devices address.  
//  Many ethernet boards will accept any address, just make sure only it is unique on your network.


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

  //nimbits settings, set the instance name (nimbits-02 is the public cloud on https://cloud.nimbits.com) the email of the account owner, and a read write id they have created.
    String instance = "nimbits-02";
    char owner[] = "OOOOOOOOOOOOOOOOO";  // add you owner id, usually in a name@gmail.com format
    String readWriteKey = "kkkkkkkkkk";  // add you id
    byte mac[] = {0x90, 0xA2, 0xDA, 0x00, 0x54, 0x39}; //this ethernet shield's MAC address

    Nimbits nimbits(instance, owner, readWriteKey);
    struct NimSeries NimSer;
    struct NimValue NimVal;
    
void setup() {
    delay(500);  // give the ethernet board time to initialize
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
  
  String TimeNow;
  int count=9;  // number of items requested
  int ret_count; // number of items received
  int i;
  float value;
    
  Serial.println("In loop");
  
  Serial.println("getting Time");
  
  TimeNow=nimbits.getTime();
  
  Serial.print("The time is now (Unix time in Millesecs->");
  Serial.println(TimeNow);
  
  Serial.println("getting Series data");
  ret_count= nimbits.getSeries(count, "pppppppppppppppppp", NimSer); // add your pointer ID

  Serial.println(NimSer.DataHeader);
  
  for(i=0;i<ret_count;i++){
    Serial.print(NimSer.Year[i]);
    Serial.print("/");
    Serial.print(NimSer.Month[i]);
    Serial.print("/");
    Serial.print(NimSer.Day[i]);
    Serial.print("    ");
    Serial.print(NimSer.Hour[i]);
    Serial.print(":");
    Serial.print(NimSer.Minute[i]);
    Serial.print(":");
    Serial.print(NimSer.Second[i]);
    Serial.print("    ");
    Serial.println(NimSer.Data[i]);
    value=atof(NimSer.Data[i]);  // convert to float and do some more stuff
    
  }
  
  int err = nimbits.getValue("ppppppppppppp", NimVal);  // add you point ID
  if (err!=0){
    Serial.print("error code after value function->");
    Serial.println(err);
  }
  Serial.print("lt->");
  Serial.println(NimVal.lt);
  Serial.print("lg->");  
  Serial.println(NimVal.lg);
  Serial.print("d->");
  Serial.println(NimVal.d);
  Serial.print("t->");
  Serial.println(NimVal.t);
  Serial.print("tSec->");
  Serial.println(NimVal.tSec);
  Serial.print("tMilles->");
  Serial.println(NimVal.tMilles);
  Serial.print("n->");
  Serial.println(NimVal.n);
  Serial.print("dx->");
  Serial.println(NimVal.dx);
  Serial.print("st->");
  Serial.println(NimVal.st);
  
  while(true);  // stop once the series data is printed 
  
  
}


