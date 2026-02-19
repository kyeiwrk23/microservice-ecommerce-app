FROM eclipse-temurin:25-jdk-ubi10-minimal
WORKDIR /app
COPY target/notification.jar notification.jar
EXPOSE 8185
ENTRYPOINT ["java", "-jar", "notification.jar"]