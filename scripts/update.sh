#!/usr/bin/env bash
#copy this file out of the source dir to ~ to update a system regularly

cd com.nimbits
git fetch
git reset --hard origin/master
cd scripts
chmod +x *.sh

./upgrade.sh
