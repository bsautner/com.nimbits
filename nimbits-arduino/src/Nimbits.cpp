#include <Arduino.h>
#include <Dhcp.h>
#include <Dns.h>
#include <EthernetServer.h>
#include <util.h>
#include <EthernetUdp.h>
#include <Ethernet.h>
#include <EthernetClient.h>
#include <SPI.h>

#include "Nimbits.h"

int PORT = 80;
const char *GOOGLE = "google.com";

const String CLIENT_TYPE_PARAM="&client=arduino";
const String APP_SPOT_DOMAIN = ".appspot.com";
const String PROTOCAL = "HTTP/1.1";

const int WAIT_TIME = 1000;

Nimbits::Nimbits(String instance, String ownerEmail, String accessKey){
_instance = instance;
_ownerEmail = ownerEmail;
_accessKey = accessKey;

}

void Nimbits::createPoint(String pointName) {
     EthernetClient client;
                       if (client.connect(GOOGLE, PORT)) {
                         client.println("POST /service/point HTTP/1.1");
                         String content;
                       //  writeAuthParams(content);
                          content += "email=";
                          content += _ownerEmail;
                          if (_accessKey.length() > 0) {
                                 content += ("&key=");
                                 content += (_accessKey);
                          }
                         content += ("&action=create");
                         content += ("&point=");
                         content += (pointName);
                          client.println("Host:nimbits1.appspot.com");
                          client.println("Connection:close");
                          client.println("Cache-Control:max-age=0");
                          client.print("Content-Type: application/x-www-form-urlencoded\n");
                          client.print("Content-Length: ");
                          client.print(content.length());
                          client.print("\n\n");
                          client.print(content);
                           while(client.connected() && !client.available()) delay(1); //waits for data
                           client.stop();
                           client.flush();
                       }



}

long Nimbits::getTime() {
    EthernetClient client;

                      if (client.connect(GOOGLE, PORT)) {
                        client.print("GET /service/time?");
                        writeAuthParams(client);
                        writeHostToClient(client);

                         String response = getResponse(client);
                         return atol(&response[0]);

                      }
                      else {
                      return -1;

                      }
 }

 float Nimbits::getValue(String pointName) {


                  EthernetClient client;

                  if (client.connect(GOOGLE, PORT)) {
                    client.print("GET /service/currentvalue?");
                    writeAuthParams(client);
                    client.print("&point=");
                    client.print(pointName);
                    writeHostToClient(client);

                     return atof(&getResponse(client)[0]);

                  }
                  else {
                  return -1;

                  }

                }

String Nimbits::getResponse(EthernetClient client) {
   String result;
   boolean inData = false;
   char c;
   while(client.connected() && !client.available()) delay(1);
                       while (client.available()) {
                         c = client.read();
                         if (c == '<') inData = true;
                         else if (inData && c != '<' && c != '>') result += c;
                         else if (inData && c == '>') client.stop();
                       }

   return result;
}

void  Nimbits::writeHostToClient(EthernetClient client) {
      client.print(CLIENT_TYPE_PARAM);
      client.print(" ");
      client.println(PROTOCAL);
      client.print("Host:");
                         client.print(_instance);
                         client.println(APP_SPOT_DOMAIN);
                         client.println();
}

void Nimbits::writeAuthParams(EthernetClient client) {
     client.print("email=");
     client.print(_ownerEmail);
     if (_accessKey.length() > 0) {
        client.print("&key=");
        client.print(_accessKey);
     }
}

String Nimbits::writeAuthParams(String content) {
     content += "email=";
      content += (_ownerEmail);
     if (_accessKey.length() > 0) {
        content += ("&key=");
        content += (_accessKey);
     }
}

//record a value

//record data

//create point

//create point with parent

//batch update

//delete point

//get children with values






