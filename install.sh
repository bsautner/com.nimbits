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

wget http://mirrors.gigenet.com/apache/tomcat/tomcat-8/v8.5.4/bin/apache-tomcat-8.5.4.tar.gz 
tar xvzf apache-tomcat-8.5.4.tar.gz
mv apache-tomcat-8.5.4 /opt/tomcat

sudo apt-get install mysql-server -y
echo "create database if not exists nimbits" | mysql -u root -p

mvn clean install
cp ./nimbits_server/target/nimbits_server.war /opt/tomcat/webapps/nimbits.war
cp ./tomcat8 /etc/init.d/tomcat8
chmod 755 /etc/init.d/tomcat8
update-rc.d tomcat8 defaults
reboot


