FROM maven:3.9.11-amazoncorretto-24-alpine AS builder

WORKDIR /build

# Copy the Maven POM files for the root and modules
COPY pom.xml ./
COPY FlightSchedule/pom.xml ./FlightSchedule/

# Modify the root pom.xml to include common and FlightSchedule modules
# Exlude other modules
RUN sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>FlightSchedule</module>\n</modules>' pom.xml

# Dependency download for module FlightSchedule
RUN mvn dependency:go-offline -B -pl :aircrowd-flight-schedule -am

# Copy source code for FlightSchedule modules
COPY FlightSchedule/src ./FlightSchedule/src

# Build the application (aircrowd-flight-schedule module)
RUN mvn clean package \
    -pl :aircrowd-flight-schedule \
    -am \
    -DskipTests \
    -Dmaven.test.skip=true \
    -Dmaven.clover.skip=true \
    -Dspotless.check.skip \
    -Dspotless.apply.skip \
    -B

FROM maven:3.9.11-amazoncorretto-24-alpine AS runtime

WORKDIR /app

EXPOSE 8080

# Copy application JAR file
COPY --from=builder /build/FlightSchedule/target/TapTaste-Kitchen-FlightSchedule*.jar /app/app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
