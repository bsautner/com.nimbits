#include <Nimbits.h>

#include <SPI.h>
#include <Ethernet.h>
#include <Time.h>
#include <stdlib.h>

const String CLIENT_TYPE_PARAM="&client=arduino";
const char *G = "google.com";
const String APP_SPOT_DOMAIN = ".appspot.com";
const int WAIT_TIME = 1000;
String _instance= "nimbits1";

//look on the back of your ethernet shield, it may have a sticker with a mac address.
byte mac[] = {
  0x90, 0xA2, 0xDA, 0x00, 0x11, 0x15};


EthernetClient client;
Nimbits nimbits("nimbits1", "bsautner@gmail.com","key1");

void setup() {

  pinMode(10, OUTPUT);   
  pinMode(4, OUTPUT);  
  digitalWrite(10, LOW);  
  digitalWrite(4, HIGH); 


  Serial.begin(9600);
  Serial.println("Welcome to Nimbits!");
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    while(true);
  }
  // give the Ethernet shield a second to initialize:
  delay(1000);

  //get the current value of a point
  float value = nimbits.getValue("TempF");
  Serial.println(value);
  float newValue = 64.0;
  delay(1000);

  String ret = nimbits.recordValue("TempF", newValue);
  Serial.println(ret);
  delay(1000);
  float value1 = nimbits.getValue("TempF");
  delay(1000);
  Serial.println(value1);
  //get the current time from the server
  time_t time = nimbits.getTime();
  Serial.println("The date on the cloud is:");
  Serial.print(month(time));
  Serial.print('/');
  Serial.print(day(time));
  Serial.print('/');
  Serial.print(year(time));


}

void loop() {

}


