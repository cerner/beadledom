#!/bin/bash

if [[ $TRAVIS_PULL_REQUEST == "false" ]]; then
    ./mvnw deploy --settings $CI_DIR/settings.xml -DperformRelease=true -DskipTests=true
    exit $?
fi
