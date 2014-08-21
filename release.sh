#!/bin/bash

########################################################
# Script to create ruta_server self-contained release  #
########################################################

MAVEN="mvn -Dmaven.test.skip=true"

RELEASE=ruta_server_`date +"%Y%m%d"`
echo "Creating release in $RELEASE"
if [ -e "$RELEASE" ]; then
	echo "Release '$RELEASE' already exists, exiting."; exit;
fi
mkdir "$RELEASE"


# package this module with appassembler
$MAVEN package appassembler:assemble
rc=$?
if [[ $rc != 0 ]] ; then
  echo "could not package release"; exit $rc
fi

mv target/appassembler/* "$RELEASE"/.

cp application.properties "$RELEASE"/.
cp README.md "$RELEASE"/.
