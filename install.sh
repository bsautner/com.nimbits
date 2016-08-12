apt-get update
apt-get upgrade -y

add-apt-repository ppa:webupd8team/java -y
apt-get update
apt-get install oracle-java8-installer -y
apt-get install tomcat7
rm -fR /var/lib/tomcat7/webapps/ROOT
mvn clean package
cp ./nimbits_server/target/nimbits_server.war /var/lib/tomcat7/webapps/ROOT.war -v
service tomcat7 restart

#set java home for tomcat
#http://askubuntu.com/questions/154953/specify-jdk-for-tomcat7
#update-alternatives --display java
#sudo vi /etc/default/tomcat7

apt-get install git maven -y
