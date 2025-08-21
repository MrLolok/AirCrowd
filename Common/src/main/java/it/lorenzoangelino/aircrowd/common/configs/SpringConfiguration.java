package it.lorenzoangelino.aircrowd.common.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.lorenzoangelino.aircrowd.common.mapper.Mapper;
import it.lorenzoangelino.aircrowd.common.spark.SparkConfig;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties
@EnableCaching
public class SpringConfiguration {

    @Bean
    @Primary
    public SparkSession sparkSession() {
        return SparkConfig.getSparkSession();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return Mapper.DEFAULT_MAPPER;
    }

    @Bean
    public ConfigProvider configProvider() {
        return ConfigProvider.getInstance();
    }
}
