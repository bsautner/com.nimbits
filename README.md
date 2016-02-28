## About

Nimbits server is a flexible API designed to store and process time and geo stamp data,
filter incoming data and trigger events based on rules.  It stored data in a way that makes
it easy to retrieve chucks of data sets using date ranges or gps coordinates.

nimbits.io is a java client that wraps the api to make it easy to automated the server
and log data from a java or android app.

More information and documentation is available at [www.nimbits.com] (http://www.nimbits.com)

[![Build Status](http://www.nimbits.com:8080/buildStatus/icon?job=nimbits_parent)](http://www.nimbits.com:8080/job/nimbits_parent/)


## Project Structure

- nimbits_core: core project containing the guts of nimbits server
- nimbits_server: an implementation of nimbits_core for J2EE servers like jetty or tomcat with an embedded H2 Database
- nimbits_gae: an implementation of nimbits_core for Google App Engine
- nimbits_io: the java client and wrapper for the API
- samples - various samples that use nimbits io or other clients to interact with the server


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

