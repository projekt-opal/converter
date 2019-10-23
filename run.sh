#!/bin/bash
mvn clean install -Pprod
docker-compose up -d rabbitmq discovery-server
sleep 20
docker-compose up -d