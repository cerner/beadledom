#!/usr/bin/env bash

if [[ "$(echo 'VERSION=${project.version}' | mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate | grep '^VERSION')" == *-SNAPSHOT ]]; then $(export IS_SNAPSHOT=true; fi