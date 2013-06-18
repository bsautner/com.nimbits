#include <Nimbits.h>

#include <Versalino.h>
#include <dht11.h>
#include <SD.h>
#include <SPI.h>
#include <Ethernet.h>
#include <LiquidCrystal.h>

const int SD_PIN = 4;
const int ETHERNET_PIN = 10;
const int TEMP_PIN = 9;

const int BUFFER = 256;

dht11 DHT11;
char settings[BUFFER]; 
const char *fileName= "CONFIG.DAT"; 

EthernetServer server(80);
EthernetClient client;
boolean doingWrite = false;
boolean newSettings = false;
boolean settingsExist;
byte mac[] = {
  0x90, 0xA2, 0xDA, 0x0D, 0x10, 0x8B};


String instance;
String email;
String hpoint;
String tpoint;
String key;
LiquidCrystal lcd(8, 7, 6, 5, 3, 2);



void displayMessage(String message1, String message2) {
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(message1);
  lcd.setCursor(0, 1);
  lcd.print(message2);
  delay(1000);

}

void setup() {
 //  Serial.begin(9600);
  doingWrite = false;
  newSettings = false;
  lcd.begin(18, 4);
  displayMessage("Welcome to Nimbits", "");
  pinMode(ETHERNET_PIN, OUTPUT);   
  pinMode(SD_PIN, OUTPUT);  
  digitalWrite(ETHERNET_PIN, LOW);  
  digitalWrite(SD_PIN, HIGH);  
   if (Ethernet.begin(mac) == 0) {
    displayMessage("Failed to configure Ethernet using DHCP", "");
    while(true);
    }
    // give the Ethernet shield a second to initialize:
    delay(1000);
  delay(1000);
  if (!SD.begin(SD_PIN)) {
    displayMessage("Failed to start SD Card!", "");
    return;
  }

  delay(100);
  DHT11.attach(TEMP_PIN);
  processSetting(settings) ;
}

void decode(char *s) {
  boolean done;
  int idx = 0;
  int d;
  for (int i = 0; i < BUFFER; i++) {
    char c1 = s[i];
    char c2 = s[i+1];
    char c3 = s[i+2];
    if (c1 == '%' && c2 == '2' && c3 == '2') {
      s[idx++] = '\"';
      i+=2;
    }
    else if (c1 == '}') {
      s[idx++] = c1;
      d = idx;

    }    
    else {   
      s[idx++] = c1;
    }
  }


} 

void clearSettings(char *s) {

  for (int i = 0; i < BUFFER; i++) {
    s[i] = '\0';
  }
}

void loop() {
  if (! doingWrite) {
    digitalWrite(ETHERNET_PIN, LOW);        
    digitalWrite(SD_PIN, HIGH);       

    EthernetClient client = server.available();
    if (client) 
    {
      boolean currentLineIsBlank = true;
      boolean inData = false;
      int idx = 0;
      while (client.connected()) {

        if (client.available()) {
          char c = client.read();
          if (! inData && c == '|') { 
            inData = true;
            clearSettings(settings);
          }
          else if (inData && c == '|') { 
            inData = false;
          }
          else if (inData && (c != '|')) {
            settings[idx++]= c;
            newSettings = true;
            doingWrite = true;
            settingsExist = true;
          }



          if ((c == '\n' && currentLineIsBlank)) {
            inData = false;  
            client.println("HTTP/1.1 200 OK");
            client.println("Content-Type: text/html");
            client.println("Connnection: close");
            client.println();
            client.println("<!DOCTYPE HTML>");

            client.println("<html>");
            client.print("<meta http-equiv=\"refresh\" content=\"2;url=http://localhost:8083/hardware/t-box.html?local=");
            client.print(Ethernet.localIP());
            if (settingsExist) {
              client.print("&settings=");
              for (int i = 0; i < BUFFER; i++) {
                char c = settings[i];
                if (c == '}') {
                  client.print(c);
                  break;
                }
                if (c != NULL && c!= '\"' ) {

                  client.print(c);
                }
                else if (c== '\"')  {
                  client.print("%22");
                }

              } 

            }
            client.print(" \">");
            //client.print("\"2;url=nimbits.com\">"); 
            client.println();

            client.println("</html>");
            break;
          }

          if (c == '\n') {
            // you're starting a new line
            currentLineIsBlank = true;
          } 
          else if (c != '\r') {
            // you've gotten a character on the current line
            currentLineIsBlank = false;
          }
        }
      }
      delay(5);
      client.stop();
    }
  }
  
    delay(1000);
    if (newSettings) {
      saveSettings(settings);
      doingWrite = false; 
      newSettings = false;
      settingsExist = true;
    }


    int chk = DHT11.read();
    if (chk == 0) {
      Nimbits nimbits(instance, email, key);

      float h =(int)DHT11.humidity; 
      char buffer[10];
      dtostrf(h,2,2,buffer);
      String str = buffer;
      displayMessage("Humidity %" + str, hpoint);     
      String response = nimbits.recordValue(hpoint, h);
      
      

      float t =(int)DHT11.temperature; 
      char buffert[10];
      dtostrf(t,2,2,buffert);
      String strt = buffert;
      displayMessage("Temp " + strt, tpoint);  
      nimbits.recordValue(tpoint, t);  
      lcd.clear();
      lcd.print(Ethernet.localIP());
      delay(1000);
      lcd.clear();

      displayMessage(instance, email);



      long time = nimbits.getTime();
      char buffer2[10];
      dtostrf(time,2,2,buffer2);
      String strt2 = buffer2;
      displayMessage("Server Time " + strt2, "");     
    }


  



}


void saveSettings(char *s) {
  digitalWrite(10, HIGH);
  doingWrite = true;
  digitalWrite(4, LOW); 
  delay(1000);
  SD.remove("CONFIG.DAT"); 
  delay(1000);
  File myFile = SD.open(fileName, FILE_WRITE);
  if (myFile) {
    decode(s);
    for (int i = 0; i < BUFFER; i++) {
      char c = s[i];
      if (c == '}') {
        myFile.print(c);
        break;
      }
      else if (c != NULL) {
        myFile.print(c);
      }

    } 
    myFile.close();
    updateSetting(s);
  } 
  else {
    myFile.close();
  }

  digitalWrite(4, HIGH); 
  digitalWrite(10, LOW);      
  displayMessage("New Settings Saved", "");

}
void updateSetting(char *s) {

  char *str;

  while ((str = strtok_r(s, ",", &s)) != NULL) {   
    String v = String(str);
   
    v.replace("\"", "");
    if (v.indexOf("instance:") > -1) {
      v.replace("instance:", "");
      instance = v;
      instance.replace("{","");
      instance.replace("}","");

    }
    else if (v.indexOf("email:") > -1) {
      v.replace("email:", "");
      email = v;
      email.replace("{","");
      email.replace("}","");

    }
    else if (v.indexOf("key:") > -1) {
      v.replace("key:", "");
      key = v;
      key.replace("{","");
      key.replace("}","");

    }
    else if (v.indexOf("tpoint:") > -1) {
      v.replace("tpoint:", "");
      tpoint = v;
      tpoint.replace("{","");
      tpoint.replace("}","");

    }
    else if (v.indexOf("hpoint:") > -1) {
      v.replace("hpoint:", "");
      hpoint = v;
      hpoint.replace("{","");
      hpoint.replace("}","");

    }
  } 

}

void processSetting(char *s) {
  File myFile = SD.open(fileName);
  if (myFile) {
    int idx = 0;
    while (myFile.available()) {
      s[idx++] = myFile.read();

    }
    updateSetting(s);
    settingsExist = true;
    myFile.close();
  } 
}






