#!/usr/bin/env bash

echo "executing deploy.sh"
echo "decrypting keyrings"
openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/pubring.gpg.enc -out $GPG_DIR/pubring.gpg -d
openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/secring.gpg.enc -out $GPG_DIR/secring.gpg -d

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "Checking out a tag $TRAVIS_TAG"
    git checkout tags/$TRAVIS_TAG -b tag_$TRAVIS_TAG
    mvn deploy --settings $GPG_DIR/settings.xml -DskipTests=true -B -U
    ./publish_docs.sh $TRAVIS_TAG
else
    echo "not on a tag -> keep snapshot version in pom.xml"
    mvn deploy --settings $GPG_DIR/settings.xml -DskipTests=true -Dgpg.skip -B -U
    ./publish_docs.sh dev
fi

