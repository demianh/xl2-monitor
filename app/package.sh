#!/bin/bash

echo ''
echo '########################################################################'
echo '####                                                                ####'
echo '####    Make project artifacts before running this script!          ####'
echo '####    IntelliJ -> Build -> Build Artifacts -> app:jar -> Build    ####'
echo '####                                                                ####'
echo '########################################################################'
echo ''

java -jar packr.jar packr-config-mac.json

zip -r -X ../xl2monitor.zip XL2Monitor.app
