#!/bin/bash
KAFKA_WORKING_DIR="/opt/kafka"
KAFKA_BIN_DIR="$KAFKA_WORKING_DIR/bin"
KAFKA_BOOTSTRAP_SERVER="kafka:9092"
TOPIC="weather-data"
PARTITIONS=3
REPLICATION_FACTOR=1

echo "Creating Kafka topics..."
$KAFKA_BIN_DIR/kafka-topics.sh --create --bootstrap-server $KAFKA_BOOTSTRAP_SERVER \
  --replication-factor $REPLICATION_FACTOR \
  --partitions $PARTITIONS \
  --topic "$TOPIC"
echo "Topic created successfully: ${TOPIC}"