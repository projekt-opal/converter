
FROM openjdk:8
COPY target/dataset-file-fetcher-1.0.0.jar ./dataset-file-fetcher.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "./dataset-file-fetcher.jar"]

#Build example:
# docker build --rm -t mnafshin/dataset-file-fetcher:latest ./dataset-file-fetcher

#Run Example:
#docker run -e RABBITMQ_PORT=5672 -e RABBITMQ_USERNAME=guest -e RABBITMQ_PASSWORD=guest -e RABBITMQ_ADDRESS=rabbitmq --network converter_default -v /data-folder-of-jena-models:/data -e RDF_FILES_PATH=/data -e PORTAL=europeandataportal -e EUREKA_URL=http://discovery-server:8761/eureka -e CONFIG_SERVER_URL=http://config-server:8888 mnafshin/dataset-file-fetcher:latest