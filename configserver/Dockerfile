FROM eclipse-temurin:25-jdk-ubi10-minimal
WORKDIR /app
COPY target/configserver.jar configserver.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "configserver.jar"]