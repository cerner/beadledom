#!/usr/bin/env bash

echo "deploying beadledom $TRAVIS_TAG to maven central"
mvn deploy --settings $GPG_DIR/settings.xml -DskipTests=true -B -U -X
${GPG_DIR}/publish_docs.sh 2.3
