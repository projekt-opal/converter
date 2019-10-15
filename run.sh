#!/bin/bash
mvn clean install -Pprod
docker-compose up -d rabbitmq
docker-compose up -d