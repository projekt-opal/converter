#!/bin/bash
mvn clean install -Pprod
docker-compose build
docker-compose up -d rabbitmq mysql es discovery-server
echo ==============
echo please add ex_logstash and bind q_logstash to it
echo ==============
sleep 60
docker-compose up -d config-server kibana logstash
sleep 10
docker-compose up -d