#!/bin/bash

# install necessary files:
# sudo apt-get install bc
# sudo apt-get install nimbits

# make this file executable:
# chmod +x server_stats.sh

# add to crontab (command: crontab -e)
# * * * * * /path/to/server_stats.sh
email='x@x.com'
token='token'
server='localhost:8080'

# get cpu usage as a percent
used_cpu_percent=`grep 'cpu ' /proc/stat | awk '{usage=($2+$4)*100/($2+$4+$5)} END {printf "%0.2f", usage}'`
echo $used_cpu_percent


# get disk use as a percent
used_disk_percent=`df -lm | awk '{if ($6 == "/") {usage=(($3/$2)*100)}} END {printf "%0.2f", usage}'`
used_disk=`df -lm | awk '{if ($6 == "/") {usage=($3/1000)}} END {printf "%0.2f", usage}'`
echo $used_disk_percent

# get current timestamp
timestamp=`date +%s%3N`
echo $timestamp

DATA="email=$email&token=$token&json=[{"key":"CPU_percent","values":[{"d":'$used_cpu_percent',"t":'$timestamp'}]},{"key":"DISK_percent","values":[{"d":'$used_disk_percent',"t":'$timestamp'}]},{"key":"DISK_used","values":[{"d":'$used_disk',"t":'$timestamp'}]}]"

echo $DATA

#post data to nimbits

curl -H "Content-Type:application/x-www-form-urlencoded" -X POST --data $DATA http://$server/service/v2/series

