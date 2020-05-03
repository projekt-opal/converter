FROM openjdk:8
COPY target/quality-metrics-service-1.0.0.jar ./quality-metrics-service.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "./quality-metrics-service.jar"]