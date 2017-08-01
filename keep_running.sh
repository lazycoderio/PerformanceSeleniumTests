#!/bin/bash
for i in {1..5000}
do
   echo "Welcome $i times"
   mvn test  -Dsurefire.suiteXmlFiles=mac-only.xml
done