#!/bin/bash
mvn clean package -DskipTests
echo $(pwd)
sudo docker build -t "ep2p/eleuth:test_1" .
sudo docker run -p 8081:8080 -v $(pwd)/workingDir:/home/ -e ARGS="--config.nodeType=RING --config.workingDir=/home --config.cn=myhost.com --spring.profiles.active=manager" "ep2p/eleuth:test_1"
