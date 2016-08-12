apt-get update
apt-get upgrade -y
apt-get install maven -y
add-apt-repository ppa:webupd8team/java -y
apt-get update
apt-get install oracle-java8-installer -y
apt-get install tomcat7
rm -fR /var/lib/tomcat7/webapps/ROOT
mvn clean package
cp ./nimbits_server/target/nimbits_server.war /var/lib/tomcat7/webapps/ROOT.war -v
service tomcat7 restart
