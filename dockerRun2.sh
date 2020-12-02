#!/bin/bash
mvn clean package -DskipTests
echo $(pwd)
sudo docker build -t "ep2p/eleuth:test_1" .
sudo docker run -p 8082:8080 -v $(pwd)/workingDir2:/home/ -e ARGS="--node.host=192.168.1.3 --node.port=8082 --config.nodeType=RING --config.workingDir=/home --config.cn=myhost.com --spring.profiles.active=manager" "ep2p/eleuth:test_1"
