#!/usr/bin/env bash

export version=4.1.2
export tomcat=8.5.13

apt-get update
apt-get upgrade -y
apt-get install git -y
apt-get install maven -y 
add-apt-repository ppa:webupd8team/java -y
apt-get update
apt-get install oracle-java8-installer -y
bash -c 'echo "JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> /etc/environment'
bash -c 'echo "CATALINA_HOME=/opt/tomcat"  >> /etc/environment'
source /etc/environment



apt-get install mysql-server -y
echo "create database if not exists nimbits" | mysql -u root -p

#if you want to install nimbits server on a device that may not have the ram to build from source, you can run these commands
#on another machine - comment out the next two lines and run them manually to build and copy the resulting .war file. 
cd ..
#mvn install:install-file -Dfile=./nimbits_server/src/main/resources/nimbits_core-${version}.out.jar -DgroupId=com.nimbits -DartifactId=nimbits_core -Dversion=${version} -Dpackaging=jar

mvn clean install

./tomcat.sh
cp ./nimbits_server/target/nimbits_server.war /opt/tomcat/webapps/nimbits.war



