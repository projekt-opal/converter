FROM openjdk:8
COPY target/elasticsearch-writer-0.0.1.jar ./elasticsearch-writer.jar
COPY run.sh /run.sh
RUN ["chmod", "+x", "./run.sh"]
ENTRYPOINT ["./run.sh"]