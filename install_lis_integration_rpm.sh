#!/bin/bash

# exit when any command fails
set -e

CONTAINER=$1
TIMEOUT=180
SECONDS=0

# Wait for the database server to start
while true
do
    echo "Checking status of the postgresql service"
    if [[ $(sudo docker exec $CONTAINER /bin/bash -c "systemctl status postgresql-9.6.service | grep \"Active: active (running)\" | wc -l") -eq 1 ]]; then
        echo "Postgresql service is active. Executing commands."
        sudo docker exec $CONTAINER /bin/bash -c "yum install -y /home/lis-integration-0.93-1.noarch.rpm"
        sudo docker exec $CONTAINER /bin/bash -c "systemctl restart lis-integration"
        sudo docker cp update_lis_integration_db.sh $CONTAINER:/home
        sudo docker exec $CONTAINER /bin/bash -c "./home/update_lis_integration_db.sh"
        exit 0
    else
        echo "The postgres service is not running! Retrying..."
        sleep 10
    fi

    if [[ $SECONDS -ge $TIMEOUT ]]; then
        echo "Timeout reached..."
        exit 1
    fi
done