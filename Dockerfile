FROM amazoncorretto:17.0.5 

WORKDIR /root

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

COPY src ./src
EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]
