#!/bin/bash
mvn clean install
sudo docker-compose build
sudo docker-compose up -d rabbitmq es discovery-server
echo ==============
echo please add ex_logstash and bind q_logstash to it
echo ==============
echo -n "Press [ENTER] to continue,...: "
# shellcheck disable=SC2162
read var_name
sudo docker-compose up -d config-server kibana logstash
sleep 10
sudo docker-compose up -d
