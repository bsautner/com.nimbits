#include <Nimbits.h>
#include <Ethernet.h>
#include <Time.h>

int waitTime = 2000;

void getTime(char *time, int max_length) {
  char c;
  boolean inData = false;
  EthernetClient client;
  int i=0;

  if (client.connect("google.com", 80)) {
    client.println("GET /service/time?client=arduino HTTP/1.1");
    client.println("Host:nimbits1.appspot.com");   
    client.println();
    delay(waitTime);
    while(client.connected() && !client.available()) delay(1); 
    while (client.available() && i < max_length) {
      c = client.read();
      if (c == '<') inData = true;
      else if (inData && c != '<' && c != '>') {time[i] = c; i++;}
      else if (inData && c == '>') client.stop();
    }
    time[i] = 0;
    client.stop();
    client.flush();
  }
  else Serial.println("connection failed");
}

int getValue(char *ID, char *result, int max_length) {
  char c;
  boolean inData = false;
  EthernetClient client;
  int i=0;

  if (  client.connect("google.com", 80)) {
    Serial.print("Downloading ");
    Serial.println(ID);
    client.print("GET /service/point?email=");
    client.print("ctcreel@gmail.com");
    client.print("&key=");
    client.print("6767aa!");
    client.print("&action=list&point=");
    client.print(ID);
    client.println("&client=arduino HTTP/1.1");
    client.println("Host:nimbits1.appspot.com");   
    client.println();
    delay(waitTime);
    while(client.connected() && !client.available()) delay(1); 
    while (client.available() && i < max_length) {
      c = client.read();
      if (c == '<') inData = true;
      else if (inData && c != '<' && c != '>') {result[i] = c; i++;}
      else if (inData && c == '>') client.stop();
    }
    result[i] = 0;
    client.stop();
    client.flush();
    //print out the points belonging to that id, the timestamp and the current value
    Serial.println(result);
  }
  else {
    Serial.println("connection failed");
  }
  return i;
}

boolean pointExists(char *ID) {
  char c;
  boolean inData = false;
  boolean found = false;
  EthernetClient client;
  char result[20];
  int i=0;

  Serial.print("in pointExists");
  if (  client.connect("google.com", 80)) {
    Serial.print("Searching for ");
    Serial.println(ID);
    client.print("GET /service/point?email=");
    client.print("ctcreel@gmail.com");
    client.print("&key=");
    client.print("6767aa!");
    client.print("&action=exists&point=");
    client.print(ID);
    client.println("&client=arduino HTTP/1.1");
    client.println("Host:nimbits1.appspot.com");   
    client.println();
    delay(waitTime);
    while(client.connected() && !client.available()) delay(1); 
    while (client.available() && i < 20) {
      c = client.read();
      if (c == '<') inData = true;
      else if (inData && c != '<' && c != '>') {result[i] = c; i++;}  
      else if (inData && c == '>') client.stop();
    }
    result[i] = 0;
    Serial.print("Got to here - ");
    Serial.println(result);
    client.stop();
    client.flush();
    //print out the points belonging to that id, the timestamp and the current value
    found = strcmp(result, "true") == 0;  
  }
  else {
    Serial.println("connection failed");
  }
  return found;
}

void createPoint(char *pointName, char *parent) {

  EthernetClient client;
  char content[100];
  
  strcpy(content,"point=");
  strcat(content, pointName);
  strcat(content, "&email=");
  strcat(content, "ctcreel@gmail.com");
  strcat(content,"&key=");
  strcat(content,"6767aa!");
  if (strlen(parent) > 0) {
      strcat(content,"&parent=");
      strcat(content,parent);
  }
  
  Serial.print(strlen(content));
  Serial.print(" - ");
  Serial.println(content);
  
  if (  client.connect("google.com", 80)) {
    client.println("POST /service/point HTTP/1.1");
    client.println("Host:nimbits1.appspot.com");
    client.println("Connection:close");
    client.println("Cache-Control:max-age=0");
    client.print("Content-Type: application/x-www-form-urlencoded\n");
    client.print("Content-Length: ");
    client.print(strlen(content));
    client.print("\n\n");
    Serial.println("ready to print content");
    client.print(content);
    Serial.println("printed content");
    while(client.connected() && !client.available()) delay(1); //waits for data
    Serial.println("got past data waiting loop");
    client.stop();
    client.flush();
    Serial.println("got past stop & flush");
  }
  else {
    Serial.println("connection failed");
  }
  
  Serial.println("Created item!!");
}

void setValue(char *pointName, char *value, char *data, char *timestamp) {

  EthernetClient client;
  char content[100];
  
  strcpy(content,"point=");
  strcat(content, pointName);
  strcat(content,"&value=");
  strcat(content, value);  
  strcat(content, "&email=");
  strcat(content,"ctcreel@gmail.com");
  strcat(content,"&key=");
  strcat(content,"6767aa!");
  if(strlen(data) > 0) {
    strcat(content,"&data=");
    strcat(content,data);
  }
  if(strlen(timestamp) > 0) {
    strcat(content,"&timestamp=");
    strcat(content,timestamp);
  }

  if (client.connect("google.com", 80)) {
    client.println("POST /service/currentvalue HTTP/1.1");
    client.println("Host:nimbits1.appspot.com");
    client.println("Connection:close");
    client.println("Cache-Control:max-age=0");
    client.print("Content-Type: application/x-www-form-urlencoded\n");
    client.print("Content-Length: ");
    client.print(strlen(content));
    client.print("\n\n");
    client.print(content);
    while(client.connected() && !client.available()) delay(1); //waits for data
    client.stop();
    client.flush();
  }
  else {
    Serial.println("connection failed");
  }
}
