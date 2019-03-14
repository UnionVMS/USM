#! /bin/bash

# ==================================================================
#
# Script used by Jenkins for promoting a deliverable (tar.gz or war)
#
# ==================================================================

# ==================================================================
# Parse parameters

readonly USAGE="Usage: $0 <pom file> <deliverable file> <version>"
readonly NB_EXPECTED_PARAMETERS=3
if [ $# -ne ${NB_EXPECTED_PARAMETERS} ]; then
	echo "Wrong number of parameters, ${NB_EXPECTED_PARAMETERS} expected, got $#"
	echo ${USAGE}
	exit 1
fi

readonly POM_FILE=$1
readonly DELIVERABLE=$2
readonly VERSION=$3
readonly DELIVERABLE_FILENAME=$(basename ${DELIVERABLE})

# ==================================================================
# Define constants

readonly REPOSITORY_ID='nexus.internal.repo' # for Nexus authentication, cf Maven settings.xml
readonly REPOSITORY_BASE_URL='http://factory-server-01:8080/nexus/content/repositories'
readonly REPOSITORY_NAME='java-releases'
readonly REPOSITORY_URL="${REPOSITORY_BASE_URL}/${REPOSITORY_NAME}"

if [ -n "$(echo ${DELIVERABLE_FILENAME} | egrep '.*\.tar\.gz$')" ]; then
	readonly PACKAGING='tar.gz'
elif [ -n "$(echo ${DELIVERABLE_FILENAME} | egrep '.*\.war$')" ]; then
	readonly PACKAGING='war'
fi

# ==================================================================
# Deploy deliverable to Releases repository

echo -e "\n\n==================== Deploying ${DELIVERABLE_FILENAME} to Nexus repository ${REPOSITORY_NAME} ====================\n"

mvn -B deploy:deploy-file -Dfile=${DELIVERABLE} \
	-DrepositoryId=${REPOSITORY_ID} \
	-Durl=${REPOSITORY_URL} \
	-DpomFile=${POM_FILE} \
	-Dversion=${VERSION} \
	-Dpackaging=${PACKAGING}

ret=$?

# ==================================================================
echo ''
if [ ${ret} -eq 0 ]; then
	echo -e "Deliverable deployed successfully"
else
	echo -e "Failed to deploy deliverable, Maven has returned code ${ret}"
fi

exit ${ret}

