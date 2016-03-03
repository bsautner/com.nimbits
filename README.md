## About

Nimbits server is a flexible API designed to store and process time and geo stamp data,
filter incoming data and trigger events based on rules.  It stored data in a way that makes
it easy to retrieve chucks of data sets using date ranges or gps coordinates.

nimbits.io is a java client that wraps the api to make it easy to automated the server
and log data from a java or android app.

More information and documentation is available at [www.nimbits.com] (http://www.nimbits.com)

[![Build Status](http://www.nimbits.com:8080/buildStatus/icon?job=nimbits_parent)](http://www.nimbits.com:8080/job/nimbits_parent/)

## Basic Concept

Nimbits is structured as a tree of entities. All entities have a name, unique id and a parent.  The top level entity is you. Entities are data points,
rule triggers, calculations, alerts or anything else.  Data Points are buckets that contain many values.  Values are structured like this:

```
{
    timestamp
    decimal value
    latitude
    longitude
    text data
    meta data

}
```

All fields are optional except the timestamp.  You record many Values into a Data Point where they are stored.  Based on rules you configure, incoming
values may be ignored (such as noise from a sensor), trigger calculations, relayed to other servers etc.


## Project Structure

- nimbits_core: core project containing the guts of nimbits server
- nimbits_server: an implementation of nimbits_core for J2EE servers like jetty or tomcat with an embedded H2 Database
- nimbits_gae: an implementation of nimbits_core for Google App Engine
- nimbits_io: the java client and wrapper for the API
- samples - various samples that use nimbits io or other clients to interact with the server

## nimbits server

### Install debian linux (ubuntu etc) 

Add the debian repository to your sources.list file

```echo "deb https://dl.bintray.com/bsautner/nimbits.deb {distribution} {components}" | sudo tee -a /etc/apt/sources.list```

then run 

```
apt-get update
apt-get install numbits
```

Your server will be running on localhost:8080 and you can configure it like any jetty based server in ```/opt/nimbits```

*please be sure to check this readme before upgraded to be notified of breaking changes*

### Install from source

Clone this repository and compule using maven

```mvn clean package```

copy the resulting war file in nimbits_server/target/nimbits_server.war to the webapps directory of a jetty or tomcat web server or any other J2EE 
server such as jboss.  Rename the file to root.war if you want to load it in the root context.

## nimbits.io client

The nimbits.io client library is a wrapper for the nimbits server API and provides deep automation of the server and methods for reading and writing data

You can import the library into your java or android project from the jcenter public repository.

[Browse Repository](https://bintray.com/bsautner/nimbits/com.nimbits.io/view)

### Maven
```
<dependency>
  <groupId>com.nimbits</groupId>
  <artifactId>nimbits_io</artifactId>
  <version>3.9.43</version>
  <type>pom</type>
</dependency>
```

### Gradle

```compile 'com.nimbits:nimbits_io:3.9.43'```

## Licence

Copyright 2016 Benjamin Sautner

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

