#!/usr/bin/env bash
#copy this file out of the source dir to ~ to update a system regularly
export version=4.1.1
cd com.nimbits
git fetch
git reset --hard origin/master
#mvn install:install-file -Dfile=com.nimbits/nimbits_server/src/main/resources/nimbits_core-${version}.out.jar -DgroupId=com.nimbits -DartifactId=nimbits_core -Dversion=${version} -Dpackaging=jar

cd scripts
chmod +x *.sh

./upgrade.sh