#!/bin/bash

# Config
PROJECT_DIR=/home/cybersco/www/mfw_usystems_ch
CONFIG_FILE=/home/cybersco/_config.mfw.php
HTACCESS_FILE=/home/cybersco/.htaccess_mfw

echo -e "Working with Directory $PROJECT_DIR\n"

# Check if directory exists and is a git repository
if [ ! -d "$PROJECT_DIR/.git" ]; then
    # Clone fresh repo if not yet a git repository
    rm -rf $PROJECT_DIR
    mkdir $PROJECT_DIR
    cd $PROJECT_DIR
    git clone --recursive https://github.com/demianh/xl2-monitor.git .
else
    # Update Source Code
    cd $PROJECT_DIR
    git status
    git pull
    git submodule update --recursive
fi


# Update Source Code
git status
git pull
git submodule update --recursive


# Install Config
cp $CONFIG_FILE $PROJECT_DIR/web/_conf.php
cp $HTACCESS_FILE $PROJECT_DIR/web/.htaccess

# Composer Install
cd $PROJECT_DIR/web
curl -sS https://getcomposer.org/installer | php
php composer.phar install


echo -e "\n ======= Installation Done ========\n"
