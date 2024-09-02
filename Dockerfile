#Build stage

FROM gradle:8.9.0-jdk21 AS BUILD
WORKDIR /usr/app/
COPY . .
RUN gradle build

# Package stage

FROM openjdk:21
ENV JAR_NAME=korys-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILD $APP_HOME .
EXPOSE 9000
ENTRYPOINT exec java -jar $APP_HOME/build/libs/$JAR_NAME