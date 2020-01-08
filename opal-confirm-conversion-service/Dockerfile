FROM openjdk:8
COPY target/opal-confirm-conversion-service-1.0.0.jar ./opal-confirm-conversion-service.jar
COPY run.sh /run.sh
RUN ["chmod", "+x", "./run.sh"]
ENTRYPOINT ["./run.sh"]