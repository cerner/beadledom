#!/usr/bin/env bash

set -e

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "Decrypting secrets ..."
    openssl aes-256-cbc -K $encrypted_4d3aca009c62_key -iv $encrypted_4d3aca009c62_iv -in $CI_DIR/deploy_site_key.enc -out $CI_DIR/deploy_site_key -d
    openssl aes-256-cbc -K $encrypted_0753278e989f_key -iv $encrypted_0753278e989f_iv -in $CI_DIR/signingkey.asc.enc -out $CI_DIR/signingkey.asc -d

    echo "Importing GPG signing key"
    gpg --fast-import $CI_DIR/signingkey.asc

    echo "deploying $TRAVIS_TAG to maven central"
    ./mvnw deploy --settings $CI_DIR/settings.xml -DattachScaladocs=true -B -U

    echo "building site"
    ${CI_DIR}/publish_site.sh $TRAVIS_TAG
else
    echo "deploying SNAPSHOT from master"
    ./mvnw deploy --settings $CI_DIR/settings.xml -Dgpg.skip -B -U
fi
