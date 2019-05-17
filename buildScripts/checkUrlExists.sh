#!/bin/sh
#
# check if program at $URL is downloadable from web.
# If we can't load it, we retry it $RETRIES times
# On a 528 status (invalid upload), we return 200 which
# should get interpreted as UNSTABLE build

URL="$1"
SLEEP_TIME=5
RETRIES=5

HTTP_STATUS_OK=200
HTTP_STATUS_INVALID_FILE_UPLOAD=528

while true; do
    HTTP_STATUS=`curl --write-out %{http_code} --silent --output /dev/null "${URL}"`

    if [ ${HTTP_STATUS} -eq ${HTTP_STATUS_OK} ]; then
        break
    fi


    RETRIES=$((RETRIES-1))
    if [ ${RETRIES} -eq 0 ]; then
        if [ ${HTTP_STATUS} -eq ${HTTP_STATUS_INVALID_FILE_UPLOAD} ]; then
            echo "Uploaded file seems to be invalid, request to '${URL}' returned HTTP Status ${HTTP_STATUS}"
            exit 200
        else
            echo "Could not download '${URL}', giving up!"
            exit 1
        fi
    fi

    echo "Could not retrieve '${URL}' (HTTP Status ${HTTP_STATUS}), sleep for ${SLEEP_TIME}s and retry a maximum of ${RETRIES} times"
    sleep ${SLEEP_TIME}
done
