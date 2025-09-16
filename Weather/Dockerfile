FROM maven:3.9.11-amazoncorretto-24-alpine AS builder

WORKDIR /build

# Copy the Maven POM files for the root and modules
COPY pom.xml ./
COPY Weather/pom.xml ./Weather/

# Modify the root pom.xml to include common and Weather modules
# Exlude other modules
RUN sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>Weather</module>\n</modules>' pom.xml

# Dependency download for module Weather
RUN mvn dependency:go-offline -B -pl :aircrowd-weather -am

# Copy source code for Weather modules
COPY Weather/src ./Weather/src

# Build the application (aircrowd-weather module)
RUN mvn clean package \
    -pl :aircrowd-weather \
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
COPY --from=builder /build/Weather/target/TapTaste-Kitchen-Weather*.jar /app/app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
