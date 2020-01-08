FROM openjdk:8
COPY target/elasticsearch-writer-1.0.0.jar ./elasticsearch-writer.jar
COPY run.sh /run.sh
RUN ["chmod", "+x", "./run.sh"]
ENTRYPOINT ["./run.sh"]