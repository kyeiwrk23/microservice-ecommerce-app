FROM eclipse-temurin:21-jdk-ubi10-minimal
WORKDIR /app
COPY target/payment.jar payment.jar
EXPOSE 8184
ENTRYPOINT ["java", "-jar", "payment.jar"]