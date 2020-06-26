#!/usr/bin/env bash
# Install the beadledom script on the users machine.
#
# There are some sanity checks to try and ensure that the script was
# downloaded and installed (copied) correctly on the users classpath.

DEFAULT_INSTALL_DIR='/usr/local/bin'
DEFAULT_REPO='http://cerner.github.io/beadledom/4.0/beadledom-archetype/downloads'

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

function get_dir() {
    printf "$CYAN Input desired install dir on your class path or enter for default [Default: $DEFAULT_INSTALL_DIR]: $RESET"
    read tmp

    if [ -n "$tmp" ]; then
        DEFAULT_INSTALL_DIR="$tmp"
    fi
    echo ; echo
}

get_dir

# Check is beadledom already exists
if hash beadledom 2>&-
then
    if [ -f "$DEFAULT_INSTALL_DIR/beadledom" ]; then
        printf "$YELLOW Previous beadledom installation found at $DEFAULT_INSTALL_DIR/beadledom\n"
        printf " Moving existing installation to $DEFAULT_INSTALL_DIR/beadledom.bak\n\n$RESET"
        mv "$DEFAULT_INSTALL_DIR/beadledom" "$DEFAULT_INSTALL_DIR/beadledom.bak"
    else
        printf "$YELLOW Previous beadledom installation found. Please remove then retry the install\n$RESET"
        exit 1
    fi
fi

# Check for curl
if hash curl 2>&-
then
    exec 3>&1
    GET=`which curl`
    printf "$CYAN Found $GREEN curl $CYAN will use $GET for download.$RESET\n\n"
    HTTP_STATUS=$($GET "-L" "-w" "%{http_code}" "-o" "$DEFAULT_INSTALL_DIR/beadledom" "$DEFAULT_REPO/beadledom")
    exec 1>&3 3>&-
else
    printf "$RED Cannot find curl which is required for installation. Aborting install.$RESET\n"
    exit 1
fi

# Check if curl returned an error and status code
if [[ $? -ne 0 ]] || ! [[ $HTTP_STATUS -ge 200 && $HTTP_STATUS -lt 300 ]]; then
    printf "$RED A fatal error occurred while downloading beadledom script. Aborting.\n$RESET"
    exit 1
fi

# Check contents of file for error response
if [[ ! -f "$DEFAULT_INSTALL_DIR/beadledom" ]]; then
    printf "$RED An error occurred while downloading the script.\n\n$RESET"
    exit 1
fi

printf "$CYAN Setting permissions on $DEFAULT_INSTALL_DIR/beadledom to 755\n$RESET"
chmod 755 "$DEFAULT_INSTALL_DIR/beadledom"

# Check if beadledom file is available globally to user
if hash beadledom 2>&-
then
    printf "$GREEN beadledom successfully installed. Use: beadledom --help for usage details.\n$RESET"
    exit 0
else
    printf "$RED an error occurred installation please remove file and reinstall."
    exit 1
fi
