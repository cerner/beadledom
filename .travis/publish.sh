#!/usr/bin/env bash

if [ -z "$SONATYPE_USERNAME" ]
then
    echo "error: please set SONATYPE_USERNAME and SONATYPE_PASSWORD environment variable"
    exit 1
fi

if [ -z "$SONATYPE_PASSWORD" ]
then
    echo "error: please set SONATYPE_PASSWORD environment variable"
    exit 1
fi

#if [[ $TRAVIS_PULL_REQUEST == "false" ]]; then
#    mvn deploy --settings $GPG_DIR/settings.xml -DperformRelease=true -DskipTests=true
#    exit $?
#fi

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings $GPG_DIR/settings.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=$TRAVIS_TAG 1>/dev/null 2>/dev/null
else
    echo "not on a tag -> keep snapshot version in pom.xml"
fi

mvn clean deploy --settings $GPG_DIR/settings.xml -DskipTests=true -B -U