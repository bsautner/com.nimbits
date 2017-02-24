#!/usr/bin/env bash
apt-get update
apt-get upgrade -y
apt-get install git -y
apt-get install maven -y 
add-apt-repository ppa:webupd8team/java -y
apt-get update
apt-get install oracle-java8-installer -y
bash -c 'echo "JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> /etc/environment'
source /etc/environment

apt-get install mysql-server -y
echo "create database if not exists nimbits" | mysql -u root -p



