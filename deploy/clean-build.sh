#!/bin/bash

cd ..

cd Payment && ./mvnw clean package -DskipTests && cd ..
cd configserver  && ./mvnw clean package -DskipTests && cd ..
cd gateway && ./mvnw clean package -DskipTests && cd ..
cd notification && ./mvnw clean package -DskipTests && cd ..
cd Order && ./mvnw clean package -DskipTests && cd ..
cd Product  && ./mvnw clean package -DskipTests && cd ..
cd user-profile && ./mvnw clean package -DskipTests && cd ..
cd eureka-server && ./mvnw clean package -DskipTests && cd ..
