package it.lorenzoangelino.aircrowd.prediction.services;

import it.lorenzoangelino.aircrowd.common.models.weather.WeatherData;
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
public class WeatherDataStorageService {

    private final SparkSession sparkSession;
    
    @Value("${weather-storage.iceberg.table.name}")
    private String tableName;
    
    @Value("${weather-storage.iceberg.table.location}")
    private String tableLocation;

    public void storeWeatherData(WeatherData weatherData) {
        try {
            log.info("Storing weather data to Iceberg table for location: {}", weatherData.location());
            createTableIfNotExists();
            
            Dataset<Row> weatherDF = sparkSession.createDataFrame(
                    Collections.singletonList(convertToRow(weatherData)),
                    getWeatherDataSchema()
            );
            
            weatherDF.writeTo(tableName).append();
            log.info("Successfully stored weather data for location: {}", weatherData.location());
        } catch (Exception e) {
            log.error("Failed to store weather data for location: {}", weatherData.location(), e);
            throw new RuntimeException("Failed to store weather data to Iceberg", e);
        }
    }
    
    private void createTableIfNotExists() {
        try {
            sparkSession.sql("DESCRIBE TABLE " + tableName);
        } catch (Exception e) {
            log.info("Creating weather Iceberg table: {}", tableName);
            
            String createTableSQL = String.format("""
                CREATE TABLE %s (
                    location STRING,
                    temperature DOUBLE,
                    humidity DOUBLE,
                    dewPoint DOUBLE,
                    precipitationProbability DOUBLE,
                    rain DOUBLE,
                    showers DOUBLE,
                    snowfall DOUBLE,
                    pressure DOUBLE,
                    cloudCover DOUBLE,
                    visibility DOUBLE,
                    windSpeed DOUBLE,
                    windDirection DOUBLE,
                    creation TIMESTAMP
                ) USING ICEBERG
                LOCATION '%s'
                """, tableName, tableLocation);
            
            sparkSession.sql(createTableSQL);
            log.info("Weather Iceberg table created successfully: {}", tableName);
        }
    }
    
    private StructType getWeatherDataSchema() {
        return new StructType()
                .add("location", DataTypes.StringType, false)
                .add("temperature", DataTypes.DoubleType, false)
                .add("humidity", DataTypes.DoubleType, false)
                .add("dewPoint", DataTypes.DoubleType, false)
                .add("precipitationProbability", DataTypes.DoubleType, false)
                .add("rain", DataTypes.DoubleType, false)
                .add("showers", DataTypes.DoubleType, false)
                .add("snowfall", DataTypes.DoubleType, false)
                .add("pressure", DataTypes.DoubleType, false)
                .add("cloudCover", DataTypes.DoubleType, false)
                .add("visibility", DataTypes.DoubleType, false)
                .add("windSpeed", DataTypes.DoubleType, false)
                .add("windDirection", DataTypes.DoubleType, false)
                .add("creation", DataTypes.TimestampType, false);
    }
    
    private Row convertToRow(WeatherData weatherData) {
        return RowFactory.create(
                weatherData.location(),
                weatherData.temperature(),
                weatherData.humidity(),
                weatherData.dewPoint(),
                weatherData.precipitationProbability(),
                weatherData.rain(),
                weatherData.showers(),
                weatherData.snowfall(),
                weatherData.pressure(),
                weatherData.cloudCover(),
                weatherData.visibility(),
                weatherData.windSpeed(),
                weatherData.windDirection(),
                Timestamp.valueOf(weatherData.creation())
        );
    }
}