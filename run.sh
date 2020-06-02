#!/bin/bash

# Add sudo if not loggin in as root user
PREFIX=''
if [ "$EUID" -ne 0 ]
  then
    PREFIX='sudo '
fi

mvn clean install
eval "${PREFIX}docker-compose build"
eval "${PREFIX}docker-compose up -d rabbitmq es discovery-server"
echo ==============
echo please add ex_logstash and bind q_logstash to it
echo ==============
echo -n "Press [ENTER] to continue,...: "
# shellcheck disable=SC2162
read var_name
eval "${PREFIX}docker-compose up -d config-server kibana logstash"
sleep 10
eval "${PREFIX}docker-compose up -d"
