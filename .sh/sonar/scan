#!/usr/bin/env bash
# Exit immediately if a simple command exits with a non-zero status
# see also: the 'set' command within the 'Shell Builtin Commands' section of `man bash`
set -o errexit

# Expects there to be a value for the environment variable `SONAR_TOKEN`.
# If this isn't set, add it as a prefix before you locally use this script.
# e.g.: `SONAR_TOKEN=1234_abcd ./.sh/uploadToSonar 12 "feature/someBranchName"`

# Applicable values:
# "PR"     - Performs a Pull Request analysis, then uploads to sonar cloud. Also requires values to
#            be set for BRANCH_NAME and PR_NUMBER.
# "BRANCH" - Performs a Branch analysis, then uploads to sonar cloud. Also requires a value to be
#            set for BRANCH_NAME.
# "LOCAL"  - Performs an analysis, then uploads to a local instance of sonarqube. Expects sonarqube
#            to be accessible at http://localhost:9000. No other properties are required.
# All other values, including case differences, won't perform a scan for sonar.
ANALYSIS_TYPE="${1}"

# The name of the target branch being scanned for sonar. Not required for "LOCAL" scans.
BRANCH_NAME="${2}"

# The GitHub PR number for the branch getting merged in. Only required for "PR" scans.
PR_NUMBER="${3}"

if [[ "${ANALYSIS_TYPE}" == "PR" ]]
then
 # PR analysis
 ./gradlew sonar \
   -Dsonar.host.url=https://sonarcloud.io/ \
   -Dsonar.token="${SONAR_TOKEN}" \
   -Dsonar.pullrequest.key="${PR_NUMBER}" \
   -Dsonar.pullrequest.branch="${BRANCH_NAME}" \
   -Dsonar.pullrequest.base=main

elif [[ "${ANALYSIS_TYPE}" == "BRANCH" ]]
then
  # Branch analysis
  ./gradlew sonar \
    -Dsonar.host.url=https://sonarcloud.io/ \
    -Dsonar.token="${SONAR_TOKEN}" \
    -Dsonar.branch.name="${BRANCH_NAME}"

elif [[ "${ANALYSIS_TYPE}" == "LOCAL" ]]
then
  ./gradlew sonar \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.token="${SONAR_TOKEN}"

fi


