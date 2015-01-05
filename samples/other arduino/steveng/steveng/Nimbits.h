/*
Nimbits.h - Library for interacting with the Nimbits Cloud Platform
Created By Benjamin Sautner, May 2012
Released into the public domain.

October 2013 - getSeries added to allow an Arduino to retrive Series data in csv format
               from the Nimbits cloud. The maximum number of data values is set to 10. 
Modified by Steven Guterman

November 2013 - getValue added to allow Arduino to retrive the latest data point value,
                timestamp, notes, etc.

                getTime added.  This is a good routine to call to see if you are 
		correctly connected to the internet

		added "client.stop()" before return in recordValue
Modified by Steven Guterman


*/


#ifndef _Nimbits_h
#define _Nimbits_h
/*
#include "Arduino.h"
#include <EthernetClient.h>
*/

#define ArraySize  10
#define MaxTextLen  160
 
    struct NimSeries{
      char DataHeader[40];
      int Year[ArraySize];
      int Month[ArraySize];
      int Day[ArraySize];
      int Hour[ArraySize];
      int Minute[ArraySize];
      int Second[ArraySize];
      char Data[ArraySize][17];
    };
    
    struct NimValue{
      float lt;
      float lg;
      float d;
      char  t[17];
      unsigned long tSec;
      int tMilles;
      char n[33];
      char dx[33];
      int st;
    };




class Nimbits {
  public:
    Nimbits(String instance, String ownerEmail, String accessKey);
    String getTime();
    void addEntityIfMissing(char *key, char *name, char *parent, int entityType, char *settings);
    void recordValue(double value, String note, char *pointId);
    int getSeries(int count, char *pointId, struct NimSeries &NimSer);
    int getValue(char *pointId, struct NimValue &NimVal);
	
  private:

};


#endif /* _Nimbits_h */
