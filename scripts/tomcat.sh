#!/usr/bin/env bash
export tomcat=8.5.13
bash -c 'echo "CATALINA_HOME=/opt/tomcat"  >> /etc/environment'
source /etc/environment

wget wget http://apache.claz.org/tomcat/tomcat-8/v8.5.13/bin/apache-tomcat-${version}.tar.gz
tar xvzf apache-tomcat-${version}.tar.gz
mv -v apache-tomcat-${version} /opt/tomcat