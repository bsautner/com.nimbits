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
	    Nimbits(String  hostname, int port, String  clientId);
		typedef void (*DataArrivedDelegate)(String data, double value);
		bool connectSocket(String points[], int count);
        bool connected();
        void disconnect();
		void monitorSocket();
		void setDataArrivedDelegate(DataArrivedDelegate dataArrivedDelegate);
		void sendSocketMessage(String data);
		void recordValue(double value, String pointId);
		double getValue(String point);
		String login(String email, String password);

	private:
        String getStringTableItem(int index);
        void sendHandshake(char path[]);
        EthernetClient ethernetClient;
        DataArrivedDelegate _dataArrivedDelegate;
        bool readHandshake();
        String readLine();
        char* parseJson(char *jsonString);
        String arrayToJson(String points[], int count);
        String floatToString(double number, uint8_t digits);

};


#endif