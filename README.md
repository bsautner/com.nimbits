<!--[![Build Status](http://www.nimbits.com:8080/buildStatus/icon?job=nimbits_parent)](http://www.nimbits.com:8080/job/nimbits_parent/)-->
[![Build Status](http://52.86.203.20:42421/buildStatus/icon?job=nimbits)](http://54.152.79.41:42421/job/nimbits/)

[Wiki](https://github.com/bsautner/com.nimbits/wiki) | [Community Forum](https://groups.google.com/forum/#!forum/nimbits) | [Bug Report](https://github.com/bsautner/com.nimbits/issues) 
 

## About

`nimbits server` is a web portal and API designed to store and process time and location stamped data,
filter incoming data and trigger events based on rules.  It stores data in a way that makes
it fast and easy to retrieve chunks of data sets using date ranges or gps coordinates.

Further, it is designed to run on small java embedded devices like a [RaspberryPi](https://www.raspberrypi.org/), J2EE servers like [Apache Tomcat](http://tomcat.apache.org/) and finally on clouds like [Google App Engine](https://cloud.google.com/appengine/) and [Amazon EC2](https://aws.amazon.com/ec2).  This lets you build a topgraphy of servers all connected to each other.  Small instances can filter noise from sensors and relay data up to larger servers for display on a website, for example.

`nimbits.io` is a java client that wraps the api to make it easy to automate the server
and log data from a java or android app. Part of the maven central repository and JCenter, it's easy to import it into your projects.
 



## Basic Concept

Nimbits is structured as a tree of entities. All entities have a name, unique id and a parent.  The top level entity is you. Entities are data points,
rule triggers, calculations, alerts or anything else.  Data Points are buckets that contain many values.  Values are structured like this:

![points](https://s3.amazonaws.com/com.nimbits.bucket/images/screenshots/points_screen.png)


```
{
   long timestamp: unix time in ms
   double value: a number value, current sensor reading, or a tempurature etc
   latitude, longitude: gps coords
   String text data: any string, JSON or XML payloads for example.
   String meta data: another handy string field for filtering data

}
```

All fields are optional except the timestamp.  You record many Values into a Data Point where they are stored.  Based on rules you configure, incoming
values may be ignored (such as noise from a sensor), trigger calculations, relayed to other servers etc.

With the API, you POST value objects that can trigger events like high alters, or run a rule like a webhook based on the incoming data. 
You can then perform GET requests to download a series of data based on filter criteria such as a date range.

## Project Structure
 
- `nimbits_server`: an implementation of nimbits_core for J2EE servers like jetty or tomcat with an embedded H2 Database
- `nimbits_io`: the java client and wrapper for the API and the object model
- `samples` - various samples that use nimbits io or other clients to interact with the server



## nimbits server

### Understanding the API

The API is a restful web service that uses HAL standards. This means that API responses include self links and navigatable links that allow 
you to browse the api as if it was a web site.  One of the best ways to understand the api is to:
 
- login to a server e.g. http://localhost:8080 
- create some objects
- browse to the root of the API: http://localhost:8080/service/v3/rest/me

The response will be your user entity and you'll be viewing the top level of your entity tree.

You can install plugins in your browser for formatting json and adding authentication headers. 

The API uses basic authentication, so you should also use a browser plugin to add a header like this:

`Authorization: Basic username:password`

or you can base64 encode your user name and password for added security:

`Authorization: Basic dm9yZGVsOnZvcmRlbA==`

You can then browse the api and see how every object can be represented by it's unique ID: 

http://localhost:8080/service/v3/rest/uuid

You can PUT updated to the object, post data values and get a series of data

For more on what you can do with the API, reference the [WIKI](https://github.com/bsautner/com.nimbits/wiki)


### Install debian linux (ubuntu etc) 

Add the debian repository to your sources.list file

```echo "deb https://dl.bintray.com/bsautner/nimbits.deb wheezy main" | sudo tee -a /etc/apt/sources.list```

then run 

```
apt-get update
apt-get install nimbits
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

[Browse bintray Repository](https://bintray.com/bsautner/nimbits/com.nimbits.io/view)

### Maven
```
<dependency>
  <groupId>com.nimbits</groupId>
  <artifactId>nimbits_io</artifactId>
  <version>3.9.56</version>
  <type>pom</type>
</dependency>
```

### Gradle

```compile 'com.nimbits:nimbits_io:3.9.56'```

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

