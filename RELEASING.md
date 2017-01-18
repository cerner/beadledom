# Releasing Beadledom

## Preparing for the Release

* A valid Sonatype user account is required to release this project. To create an account sign up with [Sonatype](https://issues.sonatype.org/secure/Signup!default.jspa).
* Install `gpg2` - we will be using this tool to automatically sign off our artifacts
	* install it via brew - `brew install gpg2`. There are other ways to install this tool but doing it via brew can help us all be in sync easily with the version of the tool we are using.
* Follow this [guide](http://central.sonatype.org/pages/working-with-pgp-signatures.html#generating-a-key-pair) to generate your own gpg key and secret.
* Execute the following commands in your terminal to prepare your travis account to deploy to maven central
    
    ```
    $ cd /path/to/beadledom
    $ gem install travis
    $ travis login // Prompts you to enter github username, password and two-factor authentication if enabled.
    $ travis enable -r <username>/<repository>
    <username>/<repository>: enabled :)
    
    $ export ENCRYPTION_PASSWORD=<password to encrypt>
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/secring.gpg -out deploy/secring.gpg.enc
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/pubring.gpg -out deploy/pubring.gpg.enc
    
    $ travis encrypt --add -r <username>/<repository> SONATYPE_USERNAME=<sonatype username>
    $ travis encrypt --add -r <username>/<repository> SONATYPE_PASSWORD=<sonatype password>
    $ travis encrypt --add -r <username>/<repository> ENCRYPTION_PASSWORD=<password to encrypt>
    $ travis encrypt --add -r <username>/<repository> GPG_KEYNAME=<gpg keyname (ex. 1C06698F)>
    $ travis encrypt --add -r <username>/<repository> GPG_PASSPHRASE=<gpg passphrase>
    ```   

## Release process

After preparing the machine follow the below steps

* Prepare the project for releasing

    ```
    mvn clean release:prepare
    ```

* Release the project

    ```
    mvn clean release:perform
    ```

* Push the local commits and tags

    ```
    git push origin master
    git push origin --tags
    ```

After pushing the tags, travis should automatically start building the tags and push the artifact to the maven central. To monitor the build process go to [travis-ci](https://travis-ci.org/) and navigate to beadledom project.

If at anytime the released need to be stopped. Cancel the maven commands and run the below command

```
mvn release:rollback
```

