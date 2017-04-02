#!/bin/bash
zip -r reports.zip Snooper/build/spoon
curl -X POST https://content.dropboxapi.com/2/files/upload \
    --header "Authorization: Bearer ${DROPBOX_ACCESS_TOKEN}" \
    --header "Dropbox-API-Arg: {\"path\": \"/builds/reports_${TRAVIS_BUILD_NUMBER}.zip\",\"mode\": \"overwrite\",\"autorename\": true,\"mute\": false}" \
    --header "Content-Type: application/octet-stream" \
    --data-binary @reports.zip