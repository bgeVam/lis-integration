#!/bin/bash

TIMEOUT=30
SECONDS=0

while true
do
    # Query "SELECT * FROM lis" is just a test query to check if the bahmni_lis database is created
    echo "Executing test query: \"SELECT * FROM lis\"."
    psql postgres -d bahmni_lis -c "SELECT * FROM lis;"
    if [[ $? -eq 0 ]]; then
        echo "Test query successful. Executing commands."
        psql postgres -d bahmni_lis -c "
        INSERT into lis VALUES (1, 'LIS', 'Example LIS', 'XXXX.XXXX.XXXX.XXXX', 12345, 3000);
        INSERT into order_type VALUES (1, 'Lab Order', 1);"
        systemctl stop iptables
        exit 0
    else
        echo "Test query failed. Retrying..."
        sleep 3
    fi

    if [[ $SECONDS -ge $TIMEOUT ]]; then
        echo "Timeout reached..."
        exit 1
    fi
done