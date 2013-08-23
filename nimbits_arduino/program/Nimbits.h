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
    String getTime();
    void addEntityIfMissing(char *key, char *name, char *parent, int entityType, char *settings);
    void recordValue(double value, String note, char *pointId);
  private:

};




#endif /* _Nimbits_h */
