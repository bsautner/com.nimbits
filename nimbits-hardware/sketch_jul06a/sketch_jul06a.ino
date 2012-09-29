     #include <SPI.h>
     #include <Ethernet.h>


     byte mac[] = {  0x90, 0xA2, 0xDA, 0x0D, 0x10, 0x8B};
     char serverName[] = "google.com";
     EthernetClient client;

     void setup() {
     Serial.begin(9600);
     
       pinMode(10, OUTPUT);   
  pinMode(4, OUTPUT);  
  digitalWrite(10, LOW);  
  digitalWrite(4, HIGH); 
     if (Ethernet.begin(mac) == 0) {
     Serial.println("Failed to configure Ethernet using DHCP");
     while(true);
     }
     // give the Ethernet shield a second to initialize:
     delay(1000);
     Serial.println("connecting...");

     // if you get a connection, report back via serial:

     if (client.connect(serverName, 80)) {
     Serial.println("connected");
     client.println("GET /service/value?point=TempF&email=bsautner@gmail.com HTTP/1.1");
     client.println("Host:nimbits1.appspot.com");
     client.println();
     }
     else {
     Serial.println("connection failed");
     }
     }

     void loop()
     {
     if (client.available()) {
     char c = client.read();
     Serial.print(c);
     }

     if (!client.connected()) {

     Serial.println("disconnecting.");
     client.stop();
     while(true);
     }
     }
