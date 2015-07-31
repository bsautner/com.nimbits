 
/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

#include "Nimbits.h"
#include <Client.h>
#include <Ethernet.h>
#include <SPI.h>
#include <PString.h>
#include <stdlib.h>
 
//nimbits settings, set the instance name (nimbits-02 is the public cloud on https://cloud.nimbits.com) the email of the account owner, and a read write key they have created.
String instance = "nimbits-02";
char owner[] = "test@example.com";
String readWriteKey = "key";
byte mac[] = {0x90, 0xA2, 0xDA, 0x00, 0x54, 0x39}; //this ethernet shield's MAC address

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
    nimbits.recordValue(random(300),"","test@example.com/lab_temp");
}

 





 


