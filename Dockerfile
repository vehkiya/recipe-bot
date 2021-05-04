FROM adoptopenjdk:14-jre-openj9 as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw clean install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM adoptopenjdk:14-jre-openj9 as create-image
LABEL maintainer="io.github.vehkiya"
ARG DATA_SOURCE=target/*/data.json
ARG JAR_FILE=target/*.jar
ENV service.integration.key=replace_me
ENV service.provider.source=data.json
COPY ${JAR_FILE} app.jar
COPY ${DATA_SOURCE} data.json
ENTRYPOINT ["java","-jar","/app.jar"]