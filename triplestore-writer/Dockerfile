FROM openjdk:8
COPY target/triplestore-writer-0.0.1.jar ./triplestore-writer.jar
COPY run.sh /run.sh
RUN ["chmod", "+x", "./run.sh"]
ENTRYPOINT ["./run.sh"]