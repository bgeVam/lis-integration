#!/bin/bash

# exit when any command fails
set -e

TIMEOUT=30
SECONDS=0

while true
do
    echo "Chech if table lis exists"
    if [[ $(psql postgres -d bahmni_lis -c "SELECT tablename FROM pg_catalog.pg_tables WHERE tablename='lis';" | grep "lis" | wc -l) -eq 1 ]]; then
        echo "Table lis exists. Executing commands."
        psql postgres -d bahmni_lis -c "
        INSERT into lis VALUES (1, 'LIS', 'Example LIS', 'XXXX.XXXX.XXXX.XXXX', 12345, 3000);
        INSERT into order_type VALUES (1, 'Lab Order', 1);"
        systemctl stop iptables
        exit 0
    else
        echo "Table lis does not exist. Retrying..."
        sleep 3
    fi

    if [[ $SECONDS -ge $TIMEOUT ]]; then
        echo "Timeout reached..."
        exit 1
    fi
done