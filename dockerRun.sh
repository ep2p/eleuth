#!/bin/bash
mvn clean package -DskipTests
echo $(pwd)
sudo docker build -t "ep2p/eleuth:test_1" .
sudo docker run -v $(pwd)/workingDir:/home/ -e ARGS="--config.nodeType=RING --config.workingDir=/home --config.cn=myhost.com --spring.profiles.active=ssl" "ep2p/eleuth:test_1"
