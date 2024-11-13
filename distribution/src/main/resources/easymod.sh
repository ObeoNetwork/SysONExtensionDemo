#!/bin/sh

java -cp syson-application-2024.11.0.jar \
     -Dloader.path='file:librariesEasyMod/' \
     -Dloader.main=org.eclipse.syson.SysONApplication \
     org.springframework.boot.loader.launch.PropertiesLauncher