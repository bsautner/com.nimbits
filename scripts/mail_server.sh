#!/usr/bin/env bash
sudo apt-get install mailutils -y
sudo cp ./config/mail/main.cf /etc/postfix/main.cf
sudo service postfix restart