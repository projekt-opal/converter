FROM openjdk:8
COPY target/quality-metrics-service-1.0.0.jar ./quality-metrics-service.jar
COPY run.sh /run.sh
RUN ["chmod", "+x", "./run.sh"]
ENTRYPOINT ["./run.sh"]