#!/usr/bin/env bash
apt-get update
apt-get upgrade -y

service tomcat8 stop
cp /opt/tomcat/webapps/nimbits/WEB-INF/classes/application.properties ~/application.properties.old
cd ..
mvn clean install
cp ./nimbits_server/target/nimbits_server.war /opt/tomcat/webapps/nimbits.war
service tomcat8 start
cp -f  ~/application.properties.old /opt/tomcat/webapps/nimbits/WEB-INF/classes/application.properties
rm -f  ~/application.properties.old
service tomcat8 restart



