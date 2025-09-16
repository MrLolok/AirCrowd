FROM maven:3.9.11-amazoncorretto-24-alpine AS builder

WORKDIR /build

# Copy the Maven POM files for the root and modules
COPY pom.xml ./
COPY PassengerFlowPrediction/pom.xml ./PassengerFlowPrediction/

# Modify the root pom.xml to include common and PassengerFlowPrediction modules
# Exlude other modules
RUN sed -i '/<modules>/,/<\/modules>/c\<modules>\n<module>PassengerFlowPrediction</module>\n</modules>' pom.xml

# Dependency download for module PassengerFlowPrediction
RUN mvn dependency:go-offline -B -pl :aircrowd-passenger-flow-prediction -am

# Copy source code for PassengerFlowPrediction modules
COPY PassengerFlowPrediction/src ./PassengerFlowPrediction/src

# Build the application (aircrowd-passenger-flow-prediction module)
RUN mvn clean package \
    -pl :aircrowd-passenger-flow-prediction \
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
COPY --from=builder /build/PassengerFlowPrediction/target/TapTaste-Kitchen-PassengerFlowPrediction*.jar /app/app.jar

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
