@echo off 

REM !!!!IMPORTANT!!!!
REM Before using this script to deploy do the following
REM Add putty to your path
REM Use puttygen to generate win_insecure_private_key.ppk from your %USERPROFILE%\.vagrant.d\insecure_private_key that comes along with vagrant.
REM !!!End of IMPORTANT!!!

REM All config is here

set MACHINE_IP=XXXX.XXXX.XXXX.XXXX
set TEMP_PACS_WAR_FOLDER=/tmp/deploy_pacs
set CWD=%1
set SCRIPTS_DIR=%CWD%/scripts/vagrant/
set KEY_FILE=%USERPROFILE%\.vagrant.d\win_insecure_private_key.ppk

if exist %KEY_FILE% (
    REM setup
    putty -ssh vagrant@%MACHINE_IP% -i %KEY_FILE% -m %SCRIPTS_DIR%/setup_environment.sh
    REM Copy PACS war to Vagrant tmp
    pscp  -i %KEY_FILE% %2 vagrant@%MACHINE_IP%:%TEMP_PACS_WAR_FOLDER%
    REM Copy PACS war to Tomcat from tmp
    putty -ssh vagrant@%MACHINE_IP% -i %KEY_FILE% -m %2
) else (
    echo Use puttygen to generate win_insecure_private_key.ppk from your %USERPROFILE%\.vagrant.d\insecure_private_key that comes along with vagrant.
)

