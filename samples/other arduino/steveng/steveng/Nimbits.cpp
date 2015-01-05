
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


#define BUFFER_SIZE 1024
#define PORT 80
#define CATEGORY 2
#define POINT 1
#define SUBSCRIPTION 5

const char *GOOGLE = "google.com";

const String CLIENT_TYPE_PARAM="&client=arduino";
const String APP_SPOT_DOMAIN = ".appspot.com";
const String PROTOCAL = "HTTP/1.1";
const int WAIT_TIME = 1000;
const char quote = 34;
const char colon = 58;
const char comma = 44;
const char dot = 46;
const char space = 32;
const char rightCurly = 125;

const int textType = 1;
const int intType = 2;
const int floatType = 3;

String _ownerEmail;
String _instance;
String _accessKey;
char _NimText[MaxTextLen];

Nimbits::Nimbits(String instance, String ownerEmail, String accessKey){
  _instance = instance;
  _ownerEmail = ownerEmail;
  _accessKey = accessKey;

}


String createSimpleJason(char *name, char *parent, int entityType) {

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

//  converts an ascii  char array to an unsigned long integer
//  no need to check data since it was checked by the prior routine
//
unsigned long atoul(char* value)
{
  unsigned long  retval;
  int i;
  boolean done;
  i=0;
  retval=0;
  done=false;
  
  while (!done){
     if  (value[i]!=NULL){
     byte ch = value[i];
     retval=10*retval+ (ch-48);
     i++;
    } else done=true;
  }

    return (retval);
}


// CharToInteger checks that a set of characters embedded in _NimText are
// valid integer and returns that value as an int
//
  int CharToInteger(int startchar, int isize)
  {
    char nine = 57;
    char zero = 48;
    char ch;
    int static value;
    boolean iok = true;
    value=0;
        
    for (int i=startchar;i<(startchar+isize);i++){
	ch = _NimText[i];  
        if(ch==space)ch=zero;
	if ((ch<zero)||(ch>nine))  {
	  iok = false;
          value = 9999;
          for (int j=0;j<(4-isize);j++){ value=value/10;}
	  i=isize+startchar+1;
	}
        else {
          value = value*10 + (ch-zero);  // CONVERTS ASCII CHAR VALUE TO INT VALUE
        }
    }
    return value;
  }

// get_line  returns a line of text received from the server.  
// it looks for only the new line character since some servers omit the CR
//

  int get_line(EthernetClient client)
  {
      byte chbyte;
      unsigned long wait_time = 15000;
      unsigned long now, then;
      boolean EOL;
      char ch;
      char cr = 13;  //carriage return
      char nl = 10;  // new line or line feed
      int static len;
      for (len=0;len<MaxTextLen;len++){
        _NimText[len]=0;
      }
      
      len=0;      
      then = millis();
      EOL = false;
     
   //   int buf_len = client.available();
   //   Serial.print("get_line: bytes available in ethernet buffer ->");
   //   Serial.println(buf_len);
    
      while (!client.available()){
        now=millis();
        if((now-then)>wait_time){
          Serial.print(" wait time of ->");
          Serial.print(wait_time/1000);
          Serial.println(" seconds exceded in get_line()");
          return len;
        }
      }
  
      do {
        ch = client.read();
        if (ch!=cr) {   //we add it to the text array
          _NimText[len]=ch;
          len++;
        }
        if (ch==nl)EOL=true;
	else if (len>=MaxTextLen){
          EOL=true;
	  Serial.println("Warning end of buffer reached before end of input in Get_Line");
	}
      }while ((client.available())&&(!EOL));
    
    return len;
  }
  
// getStatusCode returns the HTTP code from the repsonse header
// Step 1 to find the code, Step 2  get to the end of the header
// if a non-numeric code is not found a 999 will be returned.
// if no code is found a 998 will be returned
// if the end of characters is found before the end of the header a 997 is returned.
// If the code is 100 processing just continues until a 200 (OK) or error code is found
//
  int getStatusCode(EthernetClient client) {
	boolean haveCode;
	boolean EOH = false;  //End Of Header
	int len;
	int code;
        char nl = 10;
	byte chbyte;
        int iblank;

 //    	Serial.println("In getStatusCode");
        haveCode = false;
	sprintf(_NimText, "searching for http status code");

	while (!haveCode) {
          
          len=get_line(client);
          
	  if (len == 0) {   // no more data
		code=998;
		haveCode = true;
		EOH = true;
	  }
         
  // the status code should be after the first blank character after the HTTP token
          int index;
          index = strncmp(_NimText,"HTTP",4);
//          Serial.print("index->");
//          Serial.println(index);
          if (index==0){
            	iblank=0;
                for(int i=4;i<len;i++){
                  if (_NimText[i]==space){
                    iblank=i;
                    i=len;
                  }
                }
		if (iblank>0) { 
		      code = CharToInteger((iblank+1),3);
		      Serial.print("HTTP Return Code ->");
		      Serial.println(code);
		      if (code!=100) haveCode = true;
		}
		else{  //  "HTTP" was found but no code so make one up
		      code = 998;
		      haveCode = true;
		}
	  }
        }

	//we have the code so read lines to find the end of the http header
	while (!EOH) {
		len=get_line(client);
		if (_NimText[0]==nl){
		    EOH = true;
		}
		else if (len=0) {      //we have a badly formed response
			    code = 997;
			    EOH = true;
		}
	}
        return code;
  }

// getSeriesData. This routine parses the data returned from the getSeries request.
// if bad data is found "99" will be inserted

  int getSeriesData(EthernetClient client, int count, struct NimSeries &Nim)
  {
    int len,i;
    int returnCount = 0;
    char ch;
    byte EOL = 0;

//    Serial.println("In getSeriesData");

    len=get_line(client);  //this should be the header
    len=min(len,39);  // make sure we do  not write over the edge of the header

    for (i=0;i<(len-1);i++){
      Nim.DataHeader[i]=_NimText[i];
    }
    Nim.DataHeader[len]=EOL;
 
    for (i=0;i<count;i++){
      len=get_line(client);
  
      if  (len>1) {  // then we have more than the new line char so time to parse
        Nim.Year[returnCount]=CharToInteger(0,4);
        Nim.Month[returnCount]=CharToInteger(5,2);
        Nim.Day[returnCount]=CharToInteger(8,2);
        Nim.Hour[returnCount]=CharToInteger(11,2);
        Nim.Minute[returnCount]=CharToInteger(14,2);
        Nim.Second[returnCount]=CharToInteger(17,2);
        for (int j = 0;j<17;j++){
          ch = _NimText[20+j];
          Nim.Data[returnCount][j]=ch;
          if (ch==EOL) j=17;
        }
       returnCount+=1;
    } else {
        Serial.println("In GetSeriesData: end of data before end of count");
        i=count;    // time to go back - no more data
      }        
  }
    
  return returnCount;
}


//======================================================================
// get the variable name from between the quotes
// =====================================================================
void getJsonName(char* field, int fsize,int len, int &ptr){

  int quoteStart,quoteEnd;
  int i,j;
  boolean haveQuote;

  for (i=0;i<fsize;i++){
    field[i]=0;
  }
  
// find first quote

   while ((_NimText[ptr]!=quote)&&(ptr<len)){
	   ptr++;
   }

   if (ptr<len){  // a opening quote was found
   	haveQuote=true;
	quoteStart=ptr;
        ptr++;
   }

    while ((_NimText[ptr]!=quote)&&(ptr<len)&&(haveQuote)){
	   ptr++;
   }

   if (ptr<len){  // a closing quote was found
   	haveQuote=true;
	quoteEnd=ptr;
   } else haveQuote=false;

   if (haveQuote){
    j=0;
    if (((quoteEnd-quoteStart)-2)>fsize) quoteEnd=quoteStart+fsize;  // don't write past the end of the buffer
    for (i=(quoteStart+1);i<=(quoteEnd-1);i++){
      field[j]=_NimText[i];
      j++;
    }
  }
  return;
}

//======================================================================
// get the field value after the colon
// =====================================================================
void getJsonValue(char* field, int fsize,int len, int &fType, int &ptr){

  int Start,End;
  int i,j;
  boolean haveText,haveNumber,error,done;
  char ch;

  for (i=0;i<fsize;i++){
    field[i]=0;
  }

  haveText=false;
  haveNumber=false;
  error=false;
  done=false;
  i=0;

// find first non-space character
//    Serial.print("looking for non-space after position->");
//    Serial.println(ptr);
   while ((_NimText[ptr]==space)&&(ptr<len)){
	   ptr++;
   }
 
   if (ptr<len){  // a non-space character was found - lets see what it is

   	ch = _NimText[ptr];
	
        if (ch==quote){
//	  Serial.println("It is a Text field");	
	  haveText=true;
	  fType=textType;
	  haveNumber=false;
	  ptr++;
	  ch=_NimText[ptr];  // get the next character after the quote
	} else if (isdigit(ch)){
//	  Serial.println("It is a numeric field");	
	  haveNumber=true; 
	  haveText=false;
	  fType=intType;   // later if we find a dot fType will change to floatType
	} else {
	  error=true;
	  done=true;
	  Serial.print("Expected Quote or Number in JSON. Found ->");
	  Serial.println(ch);
	}
   } else {   
       error=true;
       done=true;
   }

	while(!done){
	  if(haveText){  // keep on adding the characters until the close quote is found
	     if(ch==quote){
	        done=true;
	     } else if (ch==NULL){
	        error=true;
                done=true;
	     } else {
	       field[i]=ch;
               i++;
	     }
	  } else if (haveNumber){ // keep on adding numbers until the field ends 
	    if (isdigit(ch)){
	      field[i]=ch;
              i++;
	     } else if ((ch==dot)&&(fType==intType)){ // this is the first decimal found if a second is found an error will be triggered
	       field[i]=ch;
	       i++;
	       fType=floatType;
	     } else if ((ch==comma)||(ch==space)||(ch==rightCurly)){
	       done=true;
	     } else {
	       error=true;
	       done=true;
	     }
	   }
	   if ((ch!=rightCurly)&&(ptr<(len-1))) ptr++;
	   if (ptr>=len){
	     error=true;
	     done=true;
	   } else {
	     ch=_NimText[ptr];
	   }
	 }
     if (error) Serial.println("error while parsing JSON in getValue");
     field[i]=NULL;
  return;
}


//=================================================================
// find the colon between the JSON name and value
//=================================================================

   boolean findColon(int len,int &ptr){
     
   boolean haveColon;
   // find the colon

   while ((_NimText[ptr]!=colon)&&(ptr<len)){
	   ptr++;
   }

   if (ptr<len){  // the colon was found
   	haveColon=true;
//	Serial.print("The colon was found in Position->");
//	Serial.println(ptr);
        ptr++;
   } else haveColon=false;
   
   return (haveColon);
 }

//==========================================================================
// getValueData. This routine will parse the data returned from the get value request.
// if bad information is found blanks will be returned
//
  int getValueData(EthernetClient client, struct NimValue &Nim)
  {
    int len,i,j,ptr,fieldCount,fType;
    char lt[]="lt",lg[]="lg",d[]="d",t[]="t",n[]="n",dx[]="dx",st[]="st";
    char name[9];
    char value[33];
    char temp[4];

    boolean openQuote,done;
    static int errorCode;
    byte EOL = 0;

//    Serial.println("In getValueData");
    errorCode=0;
    ptr=0;
    i=0;
    done=false;
    len=get_line(client);  //this should be the json string returned
    Serial.println(_NimText);

    // some brute force code to parse the string

    while((ptr<len)&&(!done)){
	    getJsonName(name,9,len, ptr);
//	    Serial.print("name->");
//	    Serial.println(name);
	
            if (findColon(len, ptr)){
	      getJsonValue(value,33,len,fType,ptr);
//	      Serial.print("value->");
//	      Serial.println(value);

	      if (!strcmp(name,lt)){
	  	  if (fType==floatType) Nim.lt=atof(value);
		  else Nim.lt=0;
	      } else if (!strcmp(name,lg)){
		  if (fType==floatType) Nim.lg=atof(value);
		  else Nim.lg=0;
	      } else if (!strcmp(name,d)){
		  if (fType==floatType) Nim.d=atof(value);
		  else Nim.d=0;
	      } else if (!strcmp(name,t)){
		 if (fType==intType){
		   strcpy(Nim.t,value);
		   int l=sizeof(value);
		   int k=-1;
		   for (i=0;((i<l)&&(k<0));i++){
		       if (value[i]==NULL) k=i;
		   }
		   for(i=0;i<3;i++){
		      temp[i]=value[k-3+i];
		      value[k-3+i]=NULL;
		   }
		   Nim.tSec=atoul(value);
		   Nim.tMilles=atoi(temp);
		 } else {
		   Nim.tSec=0;
		   Nim.tMilles=0;
		   strcpy(Nim.t,"                ");
		 }
              } else if (!strcmp(name,n)){
		 strcpy(Nim.n,value);
              } else if (!strcmp(name,dx)){
		 strcpy(Nim.dx,value);
     	      } else if (!strcmp(name,st)){
		 if (fType==intType) Nim.st=atoi(value);
              }	 

	    } else errorCode = 1;  // need to standardize errors;

            if ((ptr>=len)||(_NimText[ptr]==rightCurly)) done=true;
    }
         
    return errorCode;
}

void Nimbits::recordValue(double value, String note, char *pointId) {
  EthernetClient client;

  String json;
  json =  "{\"d\":\"";
  json +=floatToString(value, 4);

  json += "\",\"n\":\""; 
  json +=  note; 
  json +=  "\"}"; 
  String content;
  content = "email=";

  content += _ownerEmail;
  content += "&key=";
  content += _accessKey;
  content += "&json=";
  content += json;
  content += "&id=";
  content +=  pointId;

  Serial.println(content);

  if (client.connect(GOOGLE, PORT)) {
    client.println("POST /service/v2/value HTTP/1.1");
    client.println("Host:nimbits-02.appspot.com");
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
  client.stop();
}

void Nimbits::addEntityIfMissing(char *key, char *name, char *parent, int entityType, char *settings) {
  EthernetClient client;
  Serial.println("adding");
  String retStr;
  char c;
  // String json;
  String json;
  json =  "{\"name\":\"";
  json += name; 
  json.concat("\",\"description\":\""); 
  json +=   "na"; 
  json += "\",\"entityType\":\""; 
  json +=  String(entityType); 
  json +=  "\",\"parent\":\""; 
  json +=   parent; 
  json += "\",\"owner\":\""; 
  json +=  _ownerEmail;
  json +=  String("\",\"protectionLevel\":\"");
  json +=   "2";
  
  //return json;
  switch (entityType) {
  case 1: 
    // json = createSimpleJason(name, parent, entityType); 

    break;
  case 2: 
    // json = createSimpleJason(name, parent, entityType); 
    break;
  case 5: 
    json +=  "\",\"subscribedEntity\":\""; 
    json +=   parent; 
    json +=  "\",\"notifyMethod\":\""; 
    json +=   "0"; 
    json +=  "\",\"subscriptionType\":\""; 
    json +=   "5"; 
    json +=  "\",\"maxRepeat\":\""; 
    json +=   "30"; 
    json +=  "\",\"enabled\":\""; 
    json +=   "true"; 

    break;
  }
  json += settings;
  json +=  "\"}";
  Serial.println(json);
  String content;
  content = "email=";

  content += _ownerEmail;
  content += "&key=";
  content += _accessKey;
  content += "&json=";
  content += json;
  content += "&action=";
  content += "createmissing";
  Serial.println(content);
  if (client.connect(GOOGLE, PORT)) {
    client.println("POST /service/v2/entity HTTP/1.1");
    client.println("Host:nimbits-02.appspot.com");
    client.println("Connection:close");
    client.println("User-Agent: Arduino/1.0");
    client.println("Cache-Control:max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length: ");
    client.println(content.length());
    client.println();
    client.println(content);

    while(client.connected() && !client.available()) delay(1);
    int contentLength = 0;
    char buffer[BUFFER_SIZE];


    while (client.available() && contentLength++ < BUFFER_SIZE) {
      c = client.read();
      Serial.print(c);
      buffer[contentLength] = c;
    }
    Serial.println("getKeyFromJson");
    Serial.println(sizeof(buffer));
    int i=0;
    char item[] = {
      "\"key\":"          };
    while (i++ < sizeof(buffer) - sizeof(item)) {
      boolean found = false;
      found = false;
      for (int v = 0; v < sizeof(item) -1; v++) {

        if (buffer[i+v] != item[v]) { 
          found = false;
          break;
        }
        found = true;
      }
      if (found) {
        break;
      }


    }

    i = i + sizeof(item)-1;
    int keySize = 0;
    while (i++ < sizeof(buffer)-1) {
      if (buffer[i] == quote) {
        break;
      }
      else {
        key[keySize++] = buffer[i];
      }
    }
    key[keySize] = '\0';
    Serial.println(key);



    client.stop();
  }
  else {
    Serial.println("could not connect");

  }
  delay(1000);
}


int Nimbits::getSeries(int count, char *pointId, struct NimSeries &Nim) 
{
  EthernetClient client;
  
  int static series_count;  // returns the number of elements found or an error code
  int sindex;
  int http_status_code;
  char status_text[40];
  int getcount;
  char charCount[3];
  String get_string;
//  Serial.println("In getSeries");
  
  getcount=min(count,ArraySize);
  sprintf(charCount,"%02d",getcount);

  get_string = "GET /service/v2/series?&format=csv&count=";
  get_string += charCount;
  get_string += "&email=";
  get_string += _ownerEmail; 
  get_string += "&key=";
  get_string += _accessKey;
  get_string += "&id=";
  get_string +=  pointId;

//    Serial.print(get_string);
//    Serial.println(" HTTP/1.1");

  if (client.connect(GOOGLE, PORT)) {
    Serial.println("client connect OK");
    

    client.print(get_string);
    client.println(" HTTP/1.1");

    client.println("Host:nimbits-02.appspot.com");
    client.println("Connection:close");
    client.println("User-Agent: Arduino/1.0");
    client.println("Cache-Control:max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println();
    Serial.println("Get series request sent");
  } 
  else{
    http_status_code = 996;
    sprintf(status_text,"client.connect failed");
    series_count = -http_status_code;
    Serial.print("Error in client.connect");
    return series_count;
  }

    while(client.connected() && !client.available()) delay(1);  // wait for the response / add time out code

    http_status_code = getStatusCode(client);
    
//    Serial.print(http_status_code);

    if (http_status_code<300) {
	    series_count = getSeriesData (client, count, Nim);
    }
    else{
	    series_count = -http_status_code;
            Serial.print(_NimText);
    }
   // empty any other data in the buffer 
    while (client.available() ) {
      char c = client.read();
    //  Serial.print(c);

    }
    client.stop();
    delay(500);
  
  return series_count;
}
//==========================================================================
// getValue: get the most current value from the Nimbits cloud
//==========================================================================

int Nimbits::getValue(char *pointId, struct NimValue &Nim) 
{
  EthernetClient client;
  
  int static errorCode;  // returns the number of elements found or an error code
  int sindex;
  int http_status_code;
  char status_text[40];
  String get_string;
//  Serial.println("In getValue");

  errorCode = 0;

  get_string = "GET /service/v2/value?";
  get_string += "&email=";
  get_string += _ownerEmail; 
  get_string += "&key=";
  get_string += _accessKey;
  get_string += "&id=";
  get_string +=  pointId;

//    Serial.print(get_string);
//    Serial.println(" HTTP/1.1");

  if (client.connect(GOOGLE, PORT)) {
    Serial.println("client connect OK");
    

    client.print(get_string);
    client.println(" HTTP/1.1");

    client.println("Host:nimbits-02.appspot.com");
    client.println("Connection:close");
    client.println("User-Agent: Arduino/1.0");
    client.println("Cache-Control:max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println();
    Serial.println("Get value request sent");
  } 
  else{
    errorCode = 996;
    sprintf(status_text,"client.connect failed");
    Serial.print("Error in client.connect");
    return errorCode;
  }

    while(client.connected() && !client.available()) delay(1);  // wait for the response / add time out code

    http_status_code = getStatusCode(client);
    
//    Serial.print(http_status_code);

    if (http_status_code<300) {  // then we have a good value from the server
      errorCode = getValueData (client, Nim);
    }
    else{
      errorCode = -http_status_code;
      Serial.print(_NimText);
    }
    
    while (client.available() ) {
      char c = client.read();
    //  Serial.print(c);

    }
  client.stop();
  delay(500);
  return errorCode;
}


//==========================================================================
// getTime get the current Time in millisec from the Nimbits cloud
//==========================================================================

String Nimbits::getTime() 
{
  EthernetClient client;
  
  int static errorCode;
  int sindex;
  int http_status_code;
  String time_string;
  String request_string;
//  Serial.println("In getTime");

  errorCode = 0;

  request_string = "GET /service/v2/time?";

//    Serial.print(request_string);
//    Serial.println(" HTTP/1.1");

  if (client.connect(GOOGLE, PORT)) {
    Serial.println("client connect OK");
    

    client.print(request_string);
    client.println(" HTTP/1.1");

    client.println("Host:nimbits-02.appspot.com");
    client.println("Connection:close");
    client.println("User-Agent: Arduino/1.0");
    client.println("Cache-Control:max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println();
    Serial.println("time request sent");
  } 
  else{
    errorCode = 996;
    Serial.print("Error in client.connect");
    time_string="Client.connect failed";
  }
    if (errorCode==0){
      while(client.connected() && !client.available()) delay(1);  // wait for the response / add time out code

      http_status_code = getStatusCode(client);
    
//    Serial.print(http_status_code);

      if (http_status_code<300) {  // then we have a good value from the server
        int l = get_line(client);
	time_string = _NimText;
      }
      else{
        time_string ="error getting time. code: "+http_status_code;	    
      }
    }
    
    while (client.available() ) { // clear the buffer
      char c = client.read();
    //  Serial.print(c);

    }
  client.stop();
  delay(500);
  return time_string;
}

//get the time

//record a value

//record data

//create point

//create point with parent

//batch update

//delete point

//get children with values






























