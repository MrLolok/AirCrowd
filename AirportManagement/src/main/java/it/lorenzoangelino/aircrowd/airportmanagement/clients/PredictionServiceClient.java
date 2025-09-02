package it.lorenzoangelino.aircrowd.airportmanagement.clients;

import it.lorenzoangelino.aircrowd.airportmanagement.dto.PredictionResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "prediction-service", url = "${aircrowd.services.prediction.url:http://prediction-service}")
public interface PredictionServiceClient {

    @GetMapping("/api/v1/predictions/latest")
    List<PredictionResponse> getLatestPredictions();
}