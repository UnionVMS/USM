#! /bin/bash
 
# ==================================================================
#
# Main script for promoting all deliverables of a Jenkins build
#
# ==================================================================

# ==================================================================
# Constants

readonly DIRNAME=$(dirname $0)
readonly PROMOTE_DELIVERABLE_SCRIPT="${DIRNAME}/promote_deliverable.sh"
readonly JOB_BUILDS_FOLDER="${WORKSPACE}/../builds"
readonly ITEM_SEPARATOR='|'

# Get the promoted build number from promotion url
# for example: http://factory-server-01:8080/hudson/view/All/job/test-promotion/12/
build_number_to_promote=$(echo ${PROMOTED_URL} | sed 's|.*/\([0-9]\{1,\}\)/|\1|')
if [ -z "${build_number_to_promote}" ]; then
	echo -e "\nFailed to get build number from promotion url: ${PROMOTED_URL}\n"
	exit 1
fi

readonly BUILD_ARCHIVES_FOLDER="${JOB_BUILDS_FOLDER}/${build_number_to_promote}/archive"
if [ ! -d "${BUILD_ARCHIVES_FOLDER}" ]; then
	echo -e "\nThe archive folder does not exist: ${BUILD_ARCHIVES_FOLDER}"
	echo -e 'Please configure the job for archiving **/pom.xml,**/target/*.tar.gz,**/target/*.war\n'
	exit 1
fi

# ==================================================================
# Extract modules names from archived items, 
# for those that have a pom.xml and an associated tar.gz or war

cd ${BUILD_ARCHIVES_FOLDER}
archived_pom=$(find . -path '*/pom.xml')

for pom in ${archived_pom}
do	
	module_name=$(echo ${pom} | sed 's|^\(.*\)/pom.xml$|\1|')	
	expected_targz="${module_name}/target/*.tar.gz"
	found_targz=$(find . -path ${expected_targz})	
	if [ -n "${found_targz}" ]; then
		items_to_promote="${items_to_promote}${BUILD_ARCHIVES_FOLDER}/${pom}${ITEM_SEPARATOR}${BUILD_ARCHIVES_FOLDER}/${found_targz} "
	else
		expected_war="${module_name}/target/*.war"
		found_war=$(find . -path ${expected_war})	
		if [ -n "${found_war}" ]; then		
			items_to_promote="${items_to_promote}${BUILD_ARCHIVES_FOLDER}/${pom}${ITEM_SEPARATOR}${BUILD_ARCHIVES_FOLDER}/${found_war} "
		fi
	fi
done

if [ -z "items_to_promote" ]; then
	echo -e "\nNo deliverable were found in ${BUILD_ARCHIVES_FOLDER}\n"
	echo -e 'Please configure the job for archiving **/pom.xml,**/target/*.tar.gz,**/target/*.war\n'
	exit 1
fi

# ==================================================================
# Call promotion script on each found module

for module_items in ${items_to_promote}
do
	pom=$(echo ${module_items} | sed "s/${ITEM_SEPARATOR}.*//")	
	deliverable=$(echo ${module_items} | sed "s/.*${ITEM_SEPARATOR}//")
	
	if [ -n "${pom}" -a -n "${deliverable}" ]; then
		# ${PROMOTE_DELIVERABLE_SCRIPT} ${pom} ${deliverable} ${build_number_to_promote}
	fi

done




