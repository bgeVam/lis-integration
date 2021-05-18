#!/bin/bash

CONTAINER=$1
TIMEOUT=90
SECONDS=0

# Wait for the database server to start
while true
do
    # Query "SELECT 1" is just a test query to check if the postgres is running
    echo "Executing test query: \"SELECT 1\"."
    sudo docker exec $CONTAINER /bin/bash -c  "psql postgres -d bahmni_pacs -c \"SELECT 1;\""
    if [[ $? -eq 0 ]] && [[ $(sudo docker exec $CONTAINER /bin/bash -c "find /home/lis-integration-0.93-1.noarch.rpm | wc -l") -eq 1 ]]; then
        echo "Test query successful. Executing commands."
        sudo docker exec $CONTAINER /bin/bash -c "yum install -y /home/lis-integration-0.93-1.noarch.rpm"
        sudo docker exec $CONTAINER /bin/bash -c "systemctl restart lis-integration"
        sudo docker cp update_lis_integration_db.sh $CONTAINER:/home
        sudo docker exec $CONTAINER /bin/bash -c "./home/update_lis_integration_db.sh"
        exit 0
    else
        echo "The postgres service is not running! Test query failed. Retrying..."
        sleep 10
    fi

    if [[ $SECONDS -ge $TIMEOUT ]]; then
        echo "Timeout reached..."
        exit 1
    fi
done

