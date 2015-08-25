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


#ifndef _Nimbits_h
#define _Nimbits_h

#include <string.h>
#include <stdlib.h>
#include <WString.h>
#include <Ethernet.h>

#include "Arduino.h"



class Nimbits {
	public:
	    Nimbits(String  hostname, int port);
		typedef void (*DataArrivedDelegate)(String data, double value);
		typedef void (*StatusDelegate)(int statusCode, String statusText);
		bool connectSocket(String points[], int count);
        bool connected();
        void disconnect();
		void monitorSocket();
		void setDataArrivedDelegate(DataArrivedDelegate dataArrivedDelegate);
		void setStatusDelegate(StatusDelegate statusDelegate);
		void sendSocketMessage(String data);
		void recordValue(double value, String pointId);

		double getValue(String point);
		String login(String email, String password);
		void setAuthToken(String token);

	private:

	 DataArrivedDelegate _dataArrivedDelegate;
	 StatusDelegate _statusDelegate;
        String getStringTableItem(int index);
        void sendHandshake(char path[]);
        EthernetClient ethernetClient;

        bool readHandshake();
        String readLine();
        char* parseJson(char *jsonString);
        String arrayToJson(String points[], int count);
        String floatToString(double number, uint8_t digits);
        String getFullResponse(EthernetClient client);
        String getContent(String response);
        void doPost(EthernetClient client, String service, String content);
        void doGet(EthernetClient client, String service, String content);
        int getResponseCode(String response);

};


#endif