FROM openjdk:8
COPY target/triplestore-writer-1.0.0.jar ./triplestore-writer.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "./triplestore-writer.jar"]