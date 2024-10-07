#!/bin/sh

mvn clean package
rm distribution/libraries/easymod-distribution-2024.9.0-addon.jar
cp distribution/target/easymod-distribution-2024.9.0-addon.jar distribution/libraries
