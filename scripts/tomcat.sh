#!/usr/bin/env bash
export version=8.5.13
export url=http://www-us.apache.org/dist/tomcat/tomcat-8/v${version}/bin/apache-tomcat-${version}.tar.gz
bash -c 'echo "CATALINA_HOME=/opt/tomcat"  >> /etc/environment'
source /etc/environment

wget wget ${url}
tar xvzf apache-tomcat-${version}.tar.gz
mv -v apache-tomcat-${version} /opt/tomcat

cp -fv ./tomcat8 /etc/init.d/tomcat8
chmod 755 /etc/init.d/tomcat8
update-rc.d tomcat8 defaults