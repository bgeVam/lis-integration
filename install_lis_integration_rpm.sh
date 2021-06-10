#!/bin/bash

# exit when any command fails
set -e

TIMEOUT=180
SECONDS=0

# Wait for the database server to start
while true
do
    echo "Checking status of the postgresql service"
    if [[ $(systemctl status postgresql-9.6.service | grep "Active: active (running)" | wc -l) -eq 1 ]]; then
        echo "Postgresql service is active. Executing commands."
        yum install -y ./lis-integration-0.93-1.noarch.rpm
        systemctl restart lis-integration
        ./update_lis_integration_db.sh
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