FROM amazoncorretto:21-alpine3.20

ARG NAME=AirCrowd
ARG SERVICE=Weather
ARG VERSION=1.0.0-SNAPSHOT
ARG JAR_FILE=target/${NAME}-${SERVICE}-${VERSION}.jar

RUN mkdir -p "/home/app"
WORKDIR "/home/app"
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]