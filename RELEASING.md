# Releasing Beadledom

## Preparing for the Release

* A valid Sonatype user account is required to release this project. To create an account sign up with [Sonatype](https://issues.sonatype.org/secure/Signup!default.jspa). Sonatype requires a valid email ID with the domain `cerner.com` to confirm that we are deploying the artifacts to the right namespace.
	* **Note**: Make sure to use the Cerner email to signup.
* Execute the following commands in your terminal to prepare travis-ci
    
    ```
    $ cd /path/to/beadledom
    $ gem install travis
    $ travis login
    #  Prompts you to enter github username, password and two-factor authentication if enabled.
    $ travis enable -r <username>/<repository>
    <username>/<repository>: enabled :)
	```
* Install `gpg2` - we will be using this tool to automatically sign off our artifacts
	* install it via brew - `brew install gpg2`. There are other ways to install this tool but doing it via brew can help us all to be in sync with the version of the tool we are using.
	* Follow this [guide](http://central.sonatype.org/pages/working-with-pgp-signatures.html#generating-a-key-pair) to generate your own gpg key and secret.
	* Choose a password to encrypt the public and private keys that were generated in the previous step using gpg2. Execute the below steps to encrypt the keys.
	
	```    
    $ export ENCRYPTION_PASSWORD=<password to encrypt>
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/secring.gpg -out .travis/secring.gpg.enc
    $ openssl aes-256-cbc -pass pass:$ENCRYPTION_PASSWORD -in ~/.gnupg/pubring.gpg -out .travis/pubring.gpg.enc
	```
* All the secrets and passwords must be encrypted and passed on to travis as [secured environment variables](https://docs.travis-ci.com/user/environment-variables/#Defining-encrypted-variables-in-.travis.yml).
 
	```  
    $ travis encrypt --add -r <username>/<repository> SONATYPE_USERNAME=<sonatype username>
    $ travis encrypt --add -r <username>/<repository> SONATYPE_PASSWORD=<sonatype password>
    $ travis encrypt --add -r <username>/<repository> ENCRYPTION_PASSWORD=<password to encrypt>
    $ travis encrypt --add -r <username>/<repository> GPG_KEYNAME=<gpg keyname (ex. 1C06698F)>
    $ travis encrypt --add -r <username>/<repository> GPG_PASSPHRASE=<gpg passphrase>
    ```   
* Create a new set of ssh keys to push the documentation site to `gh-pages` branch. Follow this github [documentation](https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/#generating-a-new-ssh-key) to create the ssh keys.
	* **Note**: The ssh keys file names has to be `deploy_site`.
	* Before generating the keys make sure the current directory is the directory where all your ssh keys are stored. By default this would be `~/.ssh`
* Add the contents of `deploy_site.pub` to the [beadledom deploy keys](https://github.com/cerner/beadledom/settings/keys).
	* *pro tip*: You can copy the contents using `pbcopy < path/to/deploy_site.pub`
* Encrypt the `deploy_site` key and add it to .travis.yml file by executing the below commands.

	```
	$ cd path/to/beadledom
	$ travis encrypt-file ~/.ssh/deploy_site --add
	``` 
* Commit all the changes to the beadledom repo.
 
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

If at anytime the release need to be stopped. Cancel the maven commands and run the below command

```
mvn release:rollback
```

Once the master branch and tags are pushed the following happens.

* Travis starts a new build for released tag pushes the artifact to Maven central. It roughly takes about 2 hours for the artifacts to sync with the maven central.
* Builds the documentation site for the released tag and publishes it to `gh-pages`.
* Travis starts another build for the current snapshot and pushes the artifacts to [sonatype snapshots repo](https://oss.sonatype.org/content/repositories/snapshots/com/cerner/beadledom/).

To monitor the build go to [beadledom travis-ci builds](https://travis-ci.org/cerner/beadledom/builds).
