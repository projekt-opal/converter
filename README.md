# OPAL converter

The converter refines metadata and transforms it into 5-Star Linked Open Data. This is a publish-subscribe microservice design project via Spring Cloud and Java. The deployment of the project is based on docker-compose. Scaling can be configured manually. 

The converter integrates the following OPAL components:

- [Catfish](https://github.com/projekt-opal/catfish) (data-cleaner-service)
- [Civet](https://github.com/projekt-opal/civet) (quality-metrics-service)
- [Metadata-Refinement](https://github.com/projekt-opal/metadata-refinement) (opal-confirm-conversion-service)

## How to use

To build the project you must create a .env file and specify the required environment variables. Afterwards, by running the run.sh file it will build and setup the project. 

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
