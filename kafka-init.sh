#!/bin/bash
KAFKA_WORKING_DIR="/opt/kafka"
KAFKA_BIN_DIR="$KAFKA_WORKING_DIR/bin"
KAFKA_CONFIG_DIR="$KAFKA_WORKING_DIR/config"
KAFKA_BOOTSTRAP_SERVER="localhost:9092"
TOPICS=("weather-data" "weather-conditions")
PARTITIONS=3
REPLICATION_FACTOR=1

echo "Starting Zookeeper..."
$KAFKA_BIN_DIR/zookeeper-server-start.sh $KAFKA_CONFIG_DIR/zookeeper.properties & sleep 2

echo "Starting Kafka..."
$KAFKA_BIN_DIR/kafka-server-start.sh $KAFKA_CONFIG_DIR/server.properties &

echo "Creating Kafka topics..."
for TOPIC in "${TOPICS[@]}"; do
  $KAFKA_BIN_DIR/kafka-topics.sh --create --bootstrap-server $KAFKA_BOOTSTRAP_SERVER \
    --replication-factor $REPLICATION_FACTOR \
    --partitions $PARTITIONS \
    --topic "$TOPIC"
done
echo "Topics created successfully: ${TOPICS[*]}"

tail -f /dev/null