#!/usr/bin/env bash
export version=4.1.2
apt-get update
apt-get upgrade -y

service tomcat8 stop
cp -fv /opt/tomcat/webapps/nimbits/WEB-INF/classes/application.properties ~/application.properties.old
mvn install:install-file -Dfile=./nimbits_server/src/main/resources/nimbits_core-${version}.out.jar -DgroupId=com.nimbits -DartifactId=nimbits_core -Dversion=${version} -Dpackaging=jar

mvn -f ../pom.xml clean package
cp ../nimbits_server/target/nimbits_server.war /opt/tomcat/webapps/nimbits.war
service tomcat8 start
echo "waiting for tomcat so explode the war"
sleep 20s
cp -fv  ~/application.properties.old /opt/tomcat/webapps/nimbits/WEB-INF/classes/application.properties
rm -fv  ~/application.properties.old
service tomcat8 restart



