# FROM openjdk:21-jdk-slim
# WORKDIR /app
# COPY target/FoodApp-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "app.jar"]

#-------------TEST -----------------#

FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/FoodApp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
