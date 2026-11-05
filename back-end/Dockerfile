FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY . .

ARG SERVICE_MODULE
RUN mvn --batch-mode --no-transfer-progress \
    -f pom.xml \
    -pl "${SERVICE_MODULE}" \
    -am \
    -DskipTests \
    package

FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

ARG SERVICE_MODULE
COPY --from=build "/workspace/${SERVICE_MODULE}/target/${SERVICE_MODULE}-0.1.0-SNAPSHOT.jar" app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
