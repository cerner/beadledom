#!/usr/bin/env bash

if [[ "$(echo 'VERSION=${project.version}' | mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate | grep '^VERSION')" == *-SNAPSHOT ]];
  then
    IS_SNAPSHOT=true;
fi
if [ ! ${IS_SNAPSHOT} ];
then
  echo "stuff"
else
  echo "ding"
fi