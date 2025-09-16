FROM maven:3.9.11-amazoncorretto-24-alpine AS builder

WORKDIR /build

# Copy the Maven POM files for the root and modules
COPY pom.xml ./
COPY DiscoveryServer/pom.xml ./DiscoveryServer/

# Modify the root pom.xml to include common and DiscoveryServer modules
# Exlude other modules
RUN sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>DiscoveryServer</module>\n</modules>' pom.xml

# Dependency download for module DiscoveryServer
RUN mvn dependency:go-offline -B -pl :DiscoveryServer -am

# Copy source code for DiscoveryServer modules
COPY DiscoveryServer/src ./DiscoveryServer/src

# Build the application (aircrowd-discovery-server module)
RUN mvn clean package \
    -pl :aircrowd-discovery-server \
    -am \
    -DskipTests \
    -Dmaven.test.skip=true \
    -Dmaven.clover.skip=true \
    -Dspotless.check.skip \
    -Dspotless.apply.skip \
    -B

FROM maven:3.9.11-amazoncorretto-24-alpine AS runtime

WORKDIR /app

EXPOSE 8761

# Copy application JAR file
COPY --from=builder /build/DiscoveryServer/target/TapTaste-Kitchen-DiscoveryServer*.jar /app/app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
