FROM amazoncorretto:17.0.5 as base

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src
ARG ACCESS_KEY_ARG=notset
ARG SECRET_KEY_ARG=notset
ENV AWS_ACCESS_KEY_ID=$ACCESS_KEY_ARG
ENV AWS_SECRET_ACCESS_KEY=$SECRET_KEY_ARG
ENV AWS_REGION=eu-west-2

FROM base as test
RUN ["./mvnw", "test"]


FROM base as development
COPY ./.env ./.env
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=mysql", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'"]

FROM base as build
RUN ["./mvnw", "package", "-Dmaven.test.skip"]


FROM eclipse-temurin:17-jre-jammy as production
ARG ACCESS_KEY_ARG=notset
ARG SECRET_KEY_ARG=notset
ENV AWS_ACCESS_KEY_ID=$ACCESS_KEY_ARG
ENV AWS_SECRET_ACCESS_KEY=$SECRET_KEY_ARG
ENV AWS_REGION=eu-west-2
EXPOSE 8080
COPY --from=build /app/target/microservice-dataobject-*.jar /microservice-dataobject.jar
CMD ["java", "-jar", "/microservice-dataobject.jar"]

