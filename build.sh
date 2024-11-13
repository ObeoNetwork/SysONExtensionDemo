#!/bin/sh

mvn clean package
rm distribution/src/main/resources/librariesEasyMod/easymod-distribution-2024.11.0-addon.jar
cp distribution/target/easymod-distribution-2024.11.0-addon.jar distribution/src/main/resources/librariesEasyMod
