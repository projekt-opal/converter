#!/bin/bash
mvn clean install
docker-compose build
docker-compose up -d rabbitmq es discovery-server
echo ==============
echo please add ex_logstash and bind q_logstash to it
echo ==============
echo -n "Press [ENTER] to continue,...: "
# shellcheck disable=SC2162
read var_name
docker-compose up -d config-server kibana logstash
sleep 10
docker-compose up -d
