#!/bin/bash
sudo mkdir /var/log/converter/
sudo mkdir /var/log/converter1/
sudo mkdir /var/log/converter2/

#add opal graph to fuseki triple store
#add exchange ex_logstash to RabbitMQ Exchanges
#add queue q_logstash to RabbitMQ queues
#assign Routing key: '#' (without qoutes) to q_logstash in ex_logstash