FROM amazoncorretto:24-alpine

ARG NAME=AirCrowd
ARG SERVICE=AirportManagement
ARG VERSION=1.0.0-SNAPSHOT
ARG JAR_FILE=target/${NAME}-${SERVICE}-${VERSION}.jar

RUN mkdir -p "/home/app"
WORKDIR "/home/app"

# Copy main jar file
COPY ${JAR_FILE} app.jar

# Copy dependencies
COPY target/lib/ lib/

ENTRYPOINT ["java", "-jar", "app.jar"]