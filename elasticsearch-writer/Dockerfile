FROM openjdk:8
COPY target/elasticsearch-writer-1.0.0.jar ./elasticsearch-writer.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "./elasticsearch-writer.jar"]