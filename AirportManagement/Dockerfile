FROM maven:3.9.11-amazoncorretto-24-alpine AS builder

WORKDIR /build

# Copy the Maven POM files for the root and modules
COPY pom.xml ./
COPY AirportManagement/pom.xml ./AirportManagement/

# Modify the root pom.xml to include common and AirportManagement modules
# Exlude other modules
RUN sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>AirportManagement</module>\n</modules>' pom.xml

# Dependency download for module AirportManagement
RUN mvn dependency:go-offline -B -pl :aircrowd-airport-management -am

# Copy source code for AirportManagement modules
COPY AirportManagement/src ./AirportManagement/src

# Build the application (aircrowd-airport-management module)
RUN mvn clean package \
    -pl :aircrowd-airport-management \
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
COPY --from=builder /build/AirportManagement/target/TapTaste-Kitchen-AirportManagement*.jar /app/app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
