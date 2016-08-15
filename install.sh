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

sudo apt-get install mysql-server 
echo "create database if not exists nimbits" | mysql -u root -p

mvn clean install


 


#set java home for tomcat
#http://askubuntu.com/questions/154953/specify-jdk-for-tomcat7
#update-alternatives --display java
#sudo vi /etc/default/tomcat7


