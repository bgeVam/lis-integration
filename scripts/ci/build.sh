#!/usr/bin/env bash
echo "Removing lis integration from m2 repository."
rm -rf ~/.m2/repository/org/bahmni/module/lisintegration
rm -rf ~/.m2/repository/org.ict4h
echo "Removed lis integration from m2 repository."

echo "Building LIS"
/home/jss/apache-maven-3.0.5/bin/mvn clean install
echo "Done."