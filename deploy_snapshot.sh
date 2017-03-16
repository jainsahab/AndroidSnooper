#!/bin/bash
set -ev
if [ "${TRAVIS_PULL_REQUEST}" == "true" ]; then
    echo "Pull request: Skipping snapshot deployment:"
else
    echo "Deploying Snapshot."
    ./gradlew uploadArchives
    echo "Deployment done."
fi