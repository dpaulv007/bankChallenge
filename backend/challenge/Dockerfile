FROM maven:3.9-eclipse-temurin-8 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -DskipTests -Dmaven.test.skip=true package
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:8-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["sh","-c","java $JAVA_OPTS -jar app.jar"]
