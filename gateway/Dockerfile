FROM eclipse-temurin:24-jdk-ubi10-minimal
WORKDIR /app
COPY target/gateway.jar gateway.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "gateway.jar"]