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

prog_char stringVar[] PROGMEM = "{0}";
prog_char clientHandshakeLine1[] PROGMEM = "GET {0} HTTP/1.1";
prog_char clientHandshakeLine2[] PROGMEM = "Upgrade: WebSocket";
prog_char clientHandshakeLine3[] PROGMEM = "Connection: Upgrade";
prog_char clientHandshakeLine4[] PROGMEM = "Host: {0}";
prog_char clientHandshakeLine5[] PROGMEM = "Origin: ArduinoNimbits";
prog_char serverHandshake[] PROGMEM = "HTTP/1.1 101";

PROGMEM const char *NimbitsStringTable[] =
{   
    stringVar,
    clientHandshakeLine1,
    clientHandshakeLine2,
    clientHandshakeLine3,
    clientHandshakeLine4,
    clientHandshakeLine5,
    serverHandshake
};

String Nimbits::getStringTableItem(int index) {
    char buffer[35];
    strcpy_P(buffer, (char*)pgm_read_word(&(NimbitsStringTable[index])));
    return String(buffer);
}

bool Nimbits::connect(char hostname[], char email[], char apiKey[], char* points[], int port, char clientId[]) {
    bool result = false;
    Serial.println("OK");
    char path[1024];
    path[0] = '\0';
    byte uuidNumber[16];



     strcat(path, "/nimbits/socket?email=");
     strcat(path, email);
     strcat(path, "&API_KEY=");
     strcat(path, apiKey);
     strcat(path, "&cid=");
     strcat(path, clientId);
     strcat(path, "&format=");
     strcat(path, "simple");
     strcat(path, "&points=[");
     for (int r = 0; r < sizeof(points); r++) {
      strcat(path, "\"");
       strcat(path, points[r]);
        Serial.println(points[r]);
        if (r < sizeof(points) -1) {
         strcat(path, "\",");
        }
        else {
         strcat(path, "\"");
        }

    }
    strcat(path, "]");


    Serial.println(path);
    if (_client.connect(hostname, port)) {
        sendHandshake(hostname, path);
        result = readHandshake();
    }
    
	return result;
}


bool Nimbits::connected() {
    return _client.connected();
}

void Nimbits::disconnect() {
    _client.stop();
}

void Nimbits::monitor () {
    char character;
    
	if (_client.available() > 0 && (character = _client.read()) == 0) {
        String name = "";
        String value = "";
        char delim = ',';
        bool endReached = false;
        Serial.println("incoming data");
        int index = 0;
        while (!endReached) {
            character = _client.read();
            endReached = character == -1;

            if (!endReached) {
                if (character == delim) {
                  index++;
                }
                else if (index == 0) {
                    name += character;
                }
                else if (index == 1) {
                    value += character;
                }
            }
        }
       char carray[value.length() + 1]; //determine size of the array
       value.toCharArray(carray, sizeof(carray)); //put readStringinto an array
       float n = atof(carray); //convert the array into an Integer
        if (_dataArrivedDelegate != NULL) {
            _dataArrivedDelegate(*this, name, n);

      }
    }

}


void Nimbits::setDataArrivedDelegate(DataArrivedDelegate dataArrivedDelegate) {
	  _dataArrivedDelegate = dataArrivedDelegate;
}


void Nimbits::sendHandshake(char hostname[], char path[]) {
    String stringVar = getStringTableItem(0);
    String line1 = getStringTableItem(1);
    String line2 = getStringTableItem(2);
    String line3 = getStringTableItem(3);
    String line4 = getStringTableItem(4);
    String line5 = getStringTableItem(5);
    
    line1.replace(stringVar, path);
    line4.replace(stringVar, hostname);

     Serial.println(line1);
        Serial.println(line2);
        Serial.println(line3);
        Serial.println(line4);
        Serial.println(line5);

    _client.println(line1);
    _client.println(line2);
    _client.println(line3);
    _client.println(line4);
    _client.println(line5);
    _client.println();
}

bool Nimbits::readHandshake() {
    bool result = false;
    char character;
    String handshake = "", line;
    int maxAttempts = 300, attempts = 0;
    
    while(_client.available() == 0 && attempts < maxAttempts) 
    { 
        delay(100); 
        attempts++;
    }
    
    while((line = readLine()) != "") {
        handshake += line + '\n';
    }
    
    String response = getStringTableItem(6);
    result = handshake.indexOf(response) != -1;
    
    if(!result) {
        _client.stop();
    }
    
    return result;
}

String Nimbits::readLine() {
    String line = "";
    char character;
    
    while(_client.available() > 0 && (character = _client.read()) != '\n') {
        if (character != '\r' && character != -1) {
            line += character;
        }
    }
    
    return line;
}

void Nimbits::send (String data) {
    _client.print((char)0);
	_client.print(data);
    _client.print((char)255);
}
