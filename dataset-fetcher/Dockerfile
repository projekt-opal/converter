FROM openjdk:8
COPY target/dataset-fetcher-0.0.1.jar ./dataset-fetcher.jar
COPY run.sh /run.sh
RUN ["chmod", "+x", "./run.sh"]
ENTRYPOINT ["./run.sh"]
