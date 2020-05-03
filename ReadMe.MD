# Introduction
Converter project refines metadata and transform it into 5-Star Linked Open Data. This is a publish-subscribe microservice design project via spring cloud and java. Deployment of the project for now is based on docker-compose and scaling is possible manually. 

## How to use
To build the project you must creat .env file and put the needed environment variables there. Then, by running run.sh file it will build and setup the project. 
```
RABBITMQ_PORT=
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
CRAWLER_TRIPLESTORE_USERNAME=
CRAWLER_TRIPLESTORE_PASSWORD=
CRAWLER_TRIPLESTORE_URL=
OPAL_TRIPLESTORE_URL=
OPAL_TRIPLESTORE_USERNAME=
OPAL_TRIPLESTORE_PASSWORD=
H2_DB_PASSWORD=
ELASTICSEARCH_JAVA_OPTS=-Xms512m -Xmx512m
```
