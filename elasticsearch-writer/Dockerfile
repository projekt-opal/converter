FROM openjdk:8
COPY target/elsticsearch-writer-0.0.1.jar ./elsticsearch-writer.jar
COPY run.sh /run.sh
RUN ["chmod", "+x", "./run.sh"]
ENTRYPOINT ["./run.sh"]