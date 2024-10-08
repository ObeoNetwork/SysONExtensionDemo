#!/bin/sh

java -cp ../syson/backend/application/syson-application/target/syson-application-2024.9.1.jar \
     -Dloader.path='file:distribution/output/' \
     -Dloader.main=org.eclipse.syson.SysONApplication \
     org.springframework.boot.loader.launch.PropertiesLauncher