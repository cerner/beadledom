#!/usr/bin/env bash

set -e

# Colors
RESET='\e[0m'           # Reset
RED='\e[0;31m'          # Red
GREEN='\e[0;32m'        # Green
YELLOW='\e[0;33m'       # Yellow
BLUE='\e[0;34m'         # Blue
PURPLE='\e[0;35m'       # Magenta
CYAN='\e[0;36m'         # Cyan
WHITE='\e[0;37m'        # White
BRED='\e[1;31m'         # Bold Red
BGREEN='\e[1;32m'       # Bold Green
BYELLOW='\e[1;33m'      # Bold Yellow
BBLUE='\e[1;34m'        # Bold Blue
BPURPLE='\e[1;35m'      # Bold Magenta
BCYAN='\e[1;36m'        # Bold Cyan
BWHITE='\e[1;37m'       # Bold White

function display_usage() {
  printf "${GREEN}Usage:\n"
  printf "${YELLOW}      ./publish_site.sh [version|dev]\n\n"
  printf "${GREEN}   Example: ${YELLOW}./publish_site.sh 2.2 ${GREEN}- Prepare docs for 2.2 release.\n"
  printf "${GREEN}   Example: ${YELLOW}./publish_site.sh dev ${GREEN}- Prepare docs for current SNAPSHOT.\n$RESET"
}

function add-ssh-keys() {
  chmod 600 $CI_DIR/deploy_site_key
  eval `ssh-agent -s`
  ssh-add $CI_DIR/deploy_site_key
}

function configure-git() {
  add-ssh-keys

  git config --global user.name "travis-ci"
  git config --global user.email "travis@travis-ci.org"
}

if [[ "$#" -eq 0 ]]; then
  printf "${RED}No release tag specified\n\n"
  display_usage
  exit 1
elif [[ "$1" == "--help" ]]; then
  display_usage
  exit 0
fi

configure-git

release_tag="$1"

if [[ "$release_tag" == "dev" || "$release_tag"  == "master" ]]; then
  release_tag=$(xmllint --xpath "//*[local-name()='project']/*[local-name()='version']/text()" pom.xml)

  printf "${CYAN}Checking out master to build SNAPSHOT version ${WHITE}"${release_tag}" ${CYAN}of docs.$RESET \n"

  git checkout -B "dev"
else
  printf "${CYAN}Fetching release tags.$RESET \n"
  git fetch --tags

  tags="$(git tag -l)"

  # Set IFS to split on newlines
  oldIFS="$IFS"
  IFS='
  '
  lines=( ${tags} )

  # Reset IFS
  IFS="$oldIFS"

  if [[ ! "${lines[@]}" =~ "${release_tag}" ]]; then
    printf "${RED}Release tag ${YELLOW}${release_tag} ${CYAN}doesn't exist.$RESET \n"
    exit 1
  fi

  printf "${CYAN}Checkout out ${WHITE}${release_tag}.$RESET \n"

  git checkout -b "v${release_tag}" "${release_tag}"
fi

printf "${CYAN}Generating site.$RESET \n"
./mvnw clean site -B -q

printf "${CYAN}Staging site.$RESET \n"
./mvnw site:stage -B

printf "${CYAN}Checking out gh-pages branch.$RESET \n"
git remote set-branches --add origin gh-pages && git fetch -q
git checkout -b gh-pages origin/gh-pages

if [ ! -d "$release_tag" ]; then
  printf "${CYAN}Creating site directory ${WHITE}${release_tag}.$RESET \n"
  mkdir ${release_tag}
else
  printf "${CYAN}Clearing out existing site directory ${WHITE}${release_tag}.$RESET \n"
  rm -rf ${release_tag}/*
fi

printf "${CYAN}Moving site files to ${WHITE}${release_tag}.$RESET \n"
cp -r target/staging/* "$release_tag"/

printf "${CYAN}Creating redirecting index.html.$RESET \n"
mv target/build_index.sh "."
bash ./build_index.sh "$release_tag"

printf "${CYAN}Generating git commit.$RESET \n"
git add .
git commit -m "Rebuilt docs for Beadledom ${release_tag}"

printf "${CYAN}\nAll Done!$RESET"
printf "${CYAN}\nAutomatically pushing docs site for ${release_tag}.\n$RESET"

REPO=`git config remote.origin.url`
SSH_REPO=${REPO/https:\/\/github.com\//git@github.com:}

git push $SSH_REPO gh-pages

exit $?
