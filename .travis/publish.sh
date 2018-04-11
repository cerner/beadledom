#!/usr/bin/env bash

set -e

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "Decrypting secrets ..."
    openssl aes-256-cbc -K $encrypted_4d3aca009c62_key -iv $encrypted_4d3aca009c62_iv -in $GPG_DIR/deploy_site_key.enc -out $GPG_DIR/deploy_site_key -d
    openssl aes-256-cbc -K $encrypted_0753278e989f_key -iv $encrypted_0753278e989f_iv -in $GPG_DIR/signingkey.asc.enc -out signingkey.asc -d
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/pubring.gpg.enc -out $GPG_DIR/pubring.gpg -d
    openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in $GPG_DIR/secring.gpg.enc -out $GPG_DIR/secring.gpg -d;

    echo "Importing GPG signing key"
    gpg --fast-import $GPG_DIR/signingkey.asc

    echo "deploying $TRAVIS_TAG to maven central"
    mvn deploy --settings $GPG_DIR/settings.xml -DattachScaladocs=true -B -U

    echo "building site"
    ${GPG_DIR}/publish_site.sh $TRAVIS_TAG
else
    echo "deploying SNAPSHOT from master"
    mvn deploy --settings $GPG_DIR/settings.xml -Dgpg.skip -B -U
fi
