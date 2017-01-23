#!/usr/bin/env bash

echo "decrypting keyrings"
openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/pubring.gpg.enc -out $GPG_DIR/pubring.gpg -d
openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/secring.gpg.enc -out $GPG_DIR/secring.gpg -d

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "deploying beadledom $TRAVIS_TAG to maven central"
    git checkout tags/$TRAVIS_TAG -b tag_$TRAVIS_TAG
    mvn deploy --settings $GPG_DIR/settings.xml -DskipTests=true -B -U
    ${GPG_DIR}/publish_docs.sh $TRAVIS_TAG
else
    echo "deploying SNAPSHOT from master"
    mvn deploy --settings $GPG_DIR/settings.xml -DskipTests=true -Dgpg.skip -B -U
fi
