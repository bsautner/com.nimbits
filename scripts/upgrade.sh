#!/usr/bin/env bash
apt-get update
apt-get upgrade -y

service tomcat8 stop
cd ..
git fetch
git branch -D nimbits_update_tmp_branch
git checkout -b nimbits_update_tmp_branch
git branch -D master
git checkout -b master origin/master
mvn clean install
cp -v /opt/tomcat/webapps/nimbits/WEB-INF/applicationContext.xml /tmp/applicationContext.xml.backup
cp ./nimbits_server/target/nimbits_server.war /opt/tomcat/webapps/nimbits.war
service tomcat8 start
cp -fv /tmp/applicationContext.xml.backup /opt/tomcat/webapps/nimbits/WEB-INF/applicationContext.xml
service tomcat8 restart



