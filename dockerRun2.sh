#!/bin/bash
mvn clean package -DskipTests
mkdir -p workingDir2
sudo docker build -t "ep2p/eleuth:test_1" .
sudo docker run --add-host=node1.host.com:192.168.1.3 -p 8082:8080 -v $(pwd)/workingDir2:/home/ -e ARGS="--node.host=node2.host.com --node.port=8082 --config.nodeType=RING --config.workingDir=/home --config.cn=node2.host.com --spring.profiles.active=manager,ssl" "ep2p/eleuth:test_1"
