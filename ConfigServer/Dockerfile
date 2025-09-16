FROM maven:3.9.11-amazoncorretto-24-alpine AS builder

WORKDIR /build

# Copy the Maven POM files for the root and modules
COPY pom.xml ./
COPY ConfigServer/pom.xml ./ConfigServer/

# Modify the root pom.xml to include common and ConfigServer modules
# Exlude other modules
RUN sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>ConfigServer</module>\n</modules>' pom.xml

# Dependency download for module ConfigServer
RUN mvn dependency:go-offline -B -pl :aircrowd-config-server -am

# Copy source code for ConfigServer modules
COPY ConfigServer/src ./ConfigServer/src

# Build the application (aircrowd-config-server module)
RUN mvn clean package \
    -pl :aircrowd-config-server \
    -am \
    -DskipTests \
    -Dmaven.test.skip=true \
    -Dmaven.clover.skip=true \
    -Dspotless.check.skip \
    -Dspotless.apply.skip \
    -B

FROM maven:3.9.11-amazoncorretto-24-alpine AS runtime

WORKDIR /app

EXPOSE 8888

# Copy application JAR file
COPY --from=builder /build/ConfigServer/target/TapTaste-Kitchen-ConfigServer*.jar /app/app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
