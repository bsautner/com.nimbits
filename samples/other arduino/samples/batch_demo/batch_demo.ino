#include <Client.h>
#include <Ethernet.h>
#include <SPI.h>
#include <PString.h>
#include <stdlib.h>
 
 
byte mac[] = {0x90, 0xA2, 0xDA, 0x00, 0x54, 0x39}; //this ethernet shield's MAC address

 

void setup() {
  Serial.begin(9600); 
  Serial.println("Batch Test!");
 
  if (Ethernet.begin(mac) == 0) {
    Serial.println("DHCP Failed!");
    while(true);
  }
  randomSeed(analogRead(0));
  Serial.println("Online"); 
  delay(1000);
 
}

void loop() {
  EthernetClient client;
  String json;
  //[{"id":"test@example.com/foo","values":[{"d":0.06},{"d":0.90}]},{"id":"test@example.com/foo","values":[{"n":"notes"},{"d":0.90}]},{"id":"test@example.com/foo","values":[{"lt":0.06,"lg":0.06,"d":0.06, "dx":"blah blah data"},{"d":0.90}]},{"id":"test@example.com/bar","values":[{"d":4.3},{"d":10.0}]}]

  float val1 = random(300);
  float val2 = random(300);

  String val1Str = floatToString(val1, 4);
  String val2Str = floatToString(val2, 4);

  json = "[{";
  json += "\"id\":\"bsautner@gmail.com/lab_temp\",";
  json += "\"values\":";
  json += "[{\"d\":100}]},";
  json += "{\"id\":\"bsautner@gmail.com/lab_humidity\",";
  json += "\"values\":";
  json += "[{\"d\":42}]";
  json += "}]";
  
  String content;
  content = "email=bsautner@gmail.com";
  content += "&key=key";
  content += "&json=";
  content += json;

  Serial.println(content);

  if (client.connect("192.168.1.10", 8080)) {
    client.println("POST /service/v2/series HTTP/1.1");
   // client.println("Host:nimbits-02.appspot.com");
    client.println("Host:192.168.1.10");
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
  delay(1000);
  
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
