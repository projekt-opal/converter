FROM openjdk:8
COPY target/dataset-fetcher-1.0.0.jar ./dataset-fetcher.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "./dataset-fetcher.jar"]