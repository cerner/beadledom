#!/usr/bin/env bash

echo "deploying beadledom 2.3-Beta to maven central"
mvn deploy --settings $GPG_DIR/settings.xml -DskipTests=true -B -U
${GPG_DIR}/publish_docs.sh 2.3-Beta
