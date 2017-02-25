#!/usr/bin/env bash
apt-get update
apt-get upgrade -y

service tomcat8 stop
cd ..
git fetch
git reset --hard origin/ha
chmod +x ./scripts/*.sh
mvn clean install
cp ./nimbits_server/target/nimbits_server.war /opt/tomcat/webapps/nimbits.war
service tomcat8 start



