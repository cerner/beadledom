#!/usr/bin/env bash

set -e

if [ ! -z "$TRAVIS_TAG" ]
then
    echo "Decrypting secrets ..."
    openssl aes-256-cbc -K $encrypted_180ee38d25c2_key -iv $encrypted_180ee38d25c2_iv -in $CI_DIR/deploy_site_key.enc -out $CI_DIR/deploy_site_key -d
    openssl aes-256-cbc -K $encrypted_171b1c559d7b_key -iv $encrypted_171b1c559d7b_iv -in $CI_DIR/signingkey.asc.enc -out $CI_DIR/signingkey.asc -d

    echo "Importing GPG signing key"
    gpg --fast-import $CI_DIR/signingkey.asc

    echo "deploying $TRAVIS_TAG to maven central"
    ./mvnw deploy --settings $CI_DIR/settings.xml -DattachScaladocs=true -B -U

    echo "building site"
    ${CI_DIR}/publish_site.sh $TRAVIS_TAG
else
    echo "deploying SNAPSHOT from main"
    ./mvnw deploy --settings $CI_DIR/settings.xml -Dgpg.skip -B -U
fi
