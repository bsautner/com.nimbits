/*
 Nimbits Client
 Copyright 2014 nimbits inc.
 http://nimbits.com


 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */


#include <Nimbits.h>
#include <WString.h>
#include <string.h>
#include <stdlib.h>
#include <ArduinoJson.h>

String VALUE_API = "/service/v2/value";
String SESSION_API = "/service/v2/session";

String _hostname;
String _email;
String _password;
int _port;
String _clientId;

String _authToken;

String _key;

Nimbits::Nimbits(String  hostname, int port, String clientId){
  _hostname = hostname;

  _port = port;
  _clientId = clientId;

}


void Nimbits::setDataArrivedDelegate(DataArrivedDelegate dataArrivedDelegate) {
	  _dataArrivedDelegate = dataArrivedDelegate;
}
void Nimbits::setStatusDelegate(StatusDelegate statusDelegate) {
	  _statusDelegate = statusDelegate;
}

String Nimbits::arrayToJson(String points[], int count) {
       String retStr = "";


       retStr += "[";

       for (int i = 0; i < count; i++) {
           if (i > 0) {
              retStr += ",";
           }
            retStr += "\"";
            retStr += points[i];
            retStr += "\"";


        }
      retStr += "]";

     return retStr;
}

bool Nimbits::connectSocket(String points[], int count) {
    bool result = false;

    char path[256];
    path[0] = '\0';
    byte uuidNumber[16];
    String pointJson = arrayToJson(points, count);



     strcat(path, "/socket?AuthToken=");
     strcat(path, _authToken.c_str());
     if (count > 0) {
        strcat(path, "&points=");
        strcat(path, pointJson.c_str());
     }



    if (ethernetClient.connect(_hostname.c_str(), _port)) {
        sendHandshake(path);
        result = readHandshake();
    }
    
	return result;
}


bool Nimbits::connected() {
    return ethernetClient.connected();
}

void Nimbits::disconnect() {
    ethernetClient.stop();
}

void Nimbits::monitorSocket () {
    char character;
    
	if (ethernetClient.available() > 0 && (character = ethernetClient.read()) == 0) {
        String str = "";

        bool endReached = false;

        int index = 0;
        while (!endReached) {
            character = ethernetClient.read();
            endReached = character == -1;

            if (!endReached) {

                    str += character;

            }
        }

         int str_len = str.length() + 1;
         StaticJsonBuffer<256> jsonBuffer;
         char char_array[str_len];
         str.toCharArray(char_array, str_len);



         JsonObject& root = jsonBuffer.parseObject(char_array);

         if (!root.success()) {

           return;

         }

         double d = root["value"]["d"];
         const char* name = root["name"];

        if (_dataArrivedDelegate != NULL) {
            _dataArrivedDelegate(name, d);
         }

      }
  //  }

}




void Nimbits::sendHandshake(char path[]) {

    String line1 = "GET " + String(path) + " HTTP/1.1";

    String line2 = "Upgrade: WebSocket";
    String line3 =  "Connection: Upgrade";
    String line4 = "Host: " + _hostname;
    String line5 = "Origin: ArduinoNimbits";

    ethernetClient.println(line1);
    ethernetClient.println(line2);
    ethernetClient.println(line3);
    ethernetClient.println(line4);
    ethernetClient.println(line5);
    ethernetClient.println();
}

bool Nimbits::readHandshake() {
    bool result = false;
    char character;
    String handshake = "", line;
    int maxAttempts = 300, attempts = 0;
    
    while(ethernetClient.available() == 0 && attempts < maxAttempts)
    { 
        delay(100); 
        attempts++;
    }
    
    while((line = readLine()) != "") {
        handshake += line + '\n';
    }
    
    String response = "HTTP/1.1 101";
    result = handshake.indexOf(response) != -1;
    
    if(!result) {

        ethernetClient.stop();
    }
    
    return result;
}

String Nimbits::readLine() {
    String line = "";
    char character;
    
    while(ethernetClient.available() > 0 && (character = ethernetClient.read()) != '\n') {
        if (character != '\r' && character != -1) {
            line += character;
        }
    }
    
    return line;
}

void Nimbits::sendSocketMessage (String data) {
    ethernetClient.print((char)0);
	ethernetClient.print(data);
    ethernetClient.print((char)255);
}

//REST API


String Nimbits::login(String email, String password) {
  EthernetClient client;
  _email = email;
  _password = password;
  String content;
  content = "email=";
  content += email;
  content += "&password=";
  content += _password;




  if (client.connect(_hostname.c_str(), _port)) {

    doPost(client, SESSION_API, content);
    String response = getFullResponse(client);
    String str = getContent(response);
  Serial.println(str);
    client.stop();

          int str_len = str.length() + 1;

          StaticJsonBuffer<1024> jsonBuffer;
          char char_array[str_len];

          // Copy it over
          str.toCharArray(char_array, str_len);

          JsonObject& root = jsonBuffer.parseObject(char_array);

      if (!root.success()) {
        _authToken = "";
        return "";

      }

      _authToken = root["authToken"];

  }
  else {

    client.stop();
  }
  return _authToken;



}

void Nimbits::setAuthToken(String token) {

_authToken = token;

}
void Nimbits::recordValue(double value, String pointId) {
  EthernetClient client;

 String json;
 json =  "{\"d\":\"";
 json += floatToString(value, 4);

json +=  "\"}";
  String content;

  content += "&json=";
  content += json;
  content += "&id=";
  content +=  pointId;



  if (client.connect(_hostname.c_str(), _port)) {

    doPost(client, VALUE_API, content);

    String response = getFullResponse(client);


    client.stop();
  }
  else {

    client.stop();
  }

}

double Nimbits::getValue(String point) {
  EthernetClient client;

  String content;

  content += "&id=";
  content +=  (_email + "/" + point);

  if (client.connect(_hostname.c_str(), _port)) {


    doGet(client, VALUE_API, content);
    String response = getFullResponse(client);


    String str = getContent(response);

      client.stop();


      int str_len = str.length() + 1;
      StaticJsonBuffer<256> jsonBuffer;
      char char_array[str_len];

      // Copy it over
      str.toCharArray(char_array, str_len);

      JsonObject& root = jsonBuffer.parseObject(char_array);

  if (!root.success()) {

    return -1.0;

  }

  double d = root["d"];

  return d;


  }
  else {

    client.stop();
    return 0;
  }

}

void Nimbits::doGet(EthernetClient client, String service, String content) {

    client.println("GET " + service + "?" + content + " HTTP/1.1");
    client.println("Accept: */*");
    client.println("Host: " + _hostname + ":" + _port);
    client.println("Connection: close");
    client.println("User-Agent: Arduino/1.0");
    client.println("AuthToken: " + _authToken);
    client.println("Cache-Control: max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");

    client.println();
    client.println();
}

void Nimbits::doPost(EthernetClient client, String service, String content) {

    client.println("POST " + service + " HTTP/1.1");
    client.println("Accept: */*");
    client.println("Host: " + _hostname + ":" + _port);
    client.println("Connection: close");
    client.println("User-Agent: Arduino/1.0");
    client.println("Cache-Control: max-age=0");
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.println("AuthToken: " + _authToken);
    client.print("Content-Length: ");
    client.println(content.length());
    client.println();
    client.println(content);

}

String Nimbits::getContent(String response) {
     String str;
     int contentBodyIndex = response.lastIndexOf('\n\n');
            if (contentBodyIndex > 0) {
              str = response.substring(contentBodyIndex);

            }
     return str;

}

String Nimbits::getFullResponse(EthernetClient client) {
  char c;
  String response;
    while(client.connected() && !client.available()) delay(1);
    while (client.available()) {
           c = client.read();
           response += c;

         }

Serial.println(response);

    int responseCode = getResponseCode(response);
    return response;

}

int Nimbits::getResponseCode(String response) {


String sub = response.substring(9, 12);
int code = sub.toInt();

   if (_statusDelegate != NULL) {
        _statusDelegate(code, response.substring(12, response.lastIndexOf("\n")));
   }

   return code;
}

String Nimbits::floatToString(double number, uint8_t digits) {
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
