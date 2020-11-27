#!/bin/bash
mvn clean package -DskipTests
sudo docker build -t "ep2p/eleuth:test_1" .
sudo docker run -e ARGS="--config.nodeType=RING" "ep2p/eleuth:test_1"
