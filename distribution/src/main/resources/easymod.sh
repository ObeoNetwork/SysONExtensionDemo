#!/bin/sh

java -cp syson-application-2024.9.0.jar \
     -Dloader.path='file:librariesEasyMode/' \
     -Dloader.main=org.eclipse.syson.SysONApplication \
     org.springframework.boot.loader.launch.PropertiesLauncher