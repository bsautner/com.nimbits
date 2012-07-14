/*
Nimbits.h - Library for interacting with the Nimbits Cloud Platform
Created By Benjamin Sautner, May 2012
Released into the public domain.

*/


#ifndef _Nimbits_h
#define _Nimbits_h
#include "Arduino.h"
#include <EthernetClient.h>
class Nimbits {
  public:
    Nimbits(String instance, String ownerEmail, String accessKey);
    float getValue(String pointName);
    long getTime();
    void createPoint(String pointName);
    String recordValue(String pointName, float value);
  private:
    String _instance;
    String _ownerEmail;
    String _accessKey;
    void writeHostToClient(EthernetClient client);
    void writeAuthParamsToClient(EthernetClient client);
    String getResponse(EthernetClient client);
    String writeAuthParams(String content);
};




#endif /* _Nimbits_h */
