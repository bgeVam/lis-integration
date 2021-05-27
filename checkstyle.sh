#!/bin/bash

# exit when any command fails
set -e

# Set default en_US.UTF-8 locale for all profile to ensure database is correct provisioned (LANGUAGE is only for messages, LC_ALL overrides explicitely all other LC variables, but LANG acts as a default - therefore it is chosen).
echo "Set correct locale."
export LANGUAGE=en_US.UTF-8
export LANG=en_US.UTF-8
locale-gen en_US.UTF-8
dpkg-reconfigure --frontend=noninteractive locales


grep -q "$LOCALE_COMMAND" /etc/profile

if [ $? -eq 1 ]; then
    echo $LOCALE_COMMAND >> /etc/profile
fi

echo "Checking internet connection..."
case "$(curl -s --retry 3 --retry-max-time 5 --retry-delay 2 -I http://google.com | sed 's/^[^ ]*  *\([0-9]\).*/\1/; 1q')" in
  [23]) { echo "Connection successfully established";};;
  5) { echo "Check proxy settings!"; exit 1;};;
  *) { echo "Internet connection is down or very slow"; exit 1;};;
esac


# Setting proxy settings for maven
mkdir -p /root/.m2
cat << EOFMAVENSETTINGS > /root/.m2/custom-settings.xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <pluginGroups />
  <proxies>
    <proxy>
      <id>id-http</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>XXXX.XXXX.XXXX.XXXX</host>
      <port>800</port>
    </proxy>
    <proxy>
      <id>id-https</id>
      <active>true</active>
      <protocol>https</protocol>
      <host>XXXX.XXXX.XXXX.XXXX</host>
      <port>800</port>
    </proxy>
  </proxies>
  <servers/>
  <mirrors/>
  <profiles/>
</settings>
EOFMAVENSETTINGS

# Check the code style of lis-integration project
mvn -gs /root/.m2/custom-settings.xml clean compile checkstyle:check -Dcheckstyle
