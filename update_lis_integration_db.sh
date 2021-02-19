#Setup LIS

sleep 30s

psql postgres -d bahmni_lis -c "
INSERT into lis VALUES (1, 'LIS', 'Example LIS', 'XXXX.XXXX.XXXX.XXXX', 12345, 3000);
INSERT into order_type VALUES (1, 'Lab Order', 1);"