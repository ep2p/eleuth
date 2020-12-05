#!/bin/bash
mvn clean package -DskipTests
mkdir -p workingDir
sudo docker build -t "ep2p/eleuth:test_1" .
sudo docker run --add-host=node2.host.com:192.168.1.3 -p 8081:8080 -v $(pwd)/workingDir:/home/ -e ARGS="--node.host=node1.host.com --node.port=8081 --config.nodeType=RING --config.workingDir=/home --config.cn=node1.host.com --spring.profiles.active=manager,ssl" "ep2p/eleuth:test_1"
