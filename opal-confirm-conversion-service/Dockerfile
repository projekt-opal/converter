FROM openjdk:8
COPY target/opal-confirm-conversion-service-1.0.0.jar ./opal-confirm-conversion-service.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "./opal-confirm-conversion-service.jar"]