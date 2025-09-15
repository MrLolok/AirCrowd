package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.common.models.predictions.PassengerFlowPrediction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;

import org.apache.spark.sql.RowFactory;

@Service
@Slf4j
@RequiredArgsConstructor
public class PredictionStorageService {

    private final SparkSession sparkSession;
    
    @Value("${iceberg.table.name}")
    private String tableName;
    
    @Value("${iceberg.table.location}")
    private String tableLocation;

    public void storePrediction(PassengerFlowPrediction prediction) {
        try {
            log.info("Storing prediction to Iceberg table: {}", prediction.predictionId());
            createTableIfNotExists();
            
            Dataset<Row> predictionDF = sparkSession.createDataFrame(
                    Collections.singletonList(convertToRow(prediction)),
                    getPredictionSchema()
            );
            
            predictionDF.writeTo(tableName).append();
            log.info("Successfully stored prediction: {}", prediction.predictionId());
        } catch (Exception e) {
            log.error("Failed to store prediction: {}", prediction.predictionId(), e);
            throw new RuntimeException("Failed to store prediction to Iceberg", e);
        }
    }
    
    private void createTableIfNotExists() {
        try {
            sparkSession.sql("DESCRIBE TABLE " + tableName);
        } catch (Exception e) {
            log.info("Creating Iceberg table: {}", tableName);
            
            String createTableSQL = String.format("""
                CREATE TABLE %s (
                    predictionId STRING,
                    createdAt TIMESTAMP,
                    predictedFor TIMESTAMP,
                    criticalityLevel STRING,
                    criticalityScore DOUBLE,
                    predictedPassengerFlow INT,
                    description STRING
                ) USING ICEBERG
                LOCATION '%s'
                """, tableName, tableLocation);
            
            sparkSession.sql(createTableSQL);
            log.info("Iceberg table created successfully: {}", tableName);
        }
    }
    
    private StructType getPredictionSchema() {
        return new StructType()
                .add("predictionId", DataTypes.StringType, false)
                .add("createdAt", DataTypes.TimestampType, false)
                .add("predictedFor", DataTypes.TimestampType, false)
                .add("criticalityLevel", DataTypes.StringType, false)
                .add("criticalityScore", DataTypes.DoubleType, false)
                .add("predictedPassengerFlow", DataTypes.IntegerType, false)
                .add("description", DataTypes.StringType, true);
    }
    
    private Row convertToRow(PassengerFlowPrediction prediction) {
        return RowFactory.create(
                prediction.predictionId(),
                Timestamp.valueOf(prediction.createdAt()),
                Timestamp.valueOf(prediction.predictionTime()),
                prediction.overallCriticality().name(),
                prediction.criticalityScore(),
                prediction.predictedPassengerFlow(),
                prediction.description()
        );
    }
}