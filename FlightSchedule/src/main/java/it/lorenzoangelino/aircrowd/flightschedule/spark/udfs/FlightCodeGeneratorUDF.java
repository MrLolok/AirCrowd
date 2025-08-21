package it.lorenzoangelino.aircrowd.flightschedule.spark.udfs;

import java.util.Random;
import org.apache.spark.sql.api.java.UDF1;

public class FlightCodeGeneratorUDF implements UDF1<Void, String> {
    @Override
    public String call(Void aVoid) {
        Random random = new Random();
        char letter1 = (char) ('A' + random.nextInt(26));
        char letter2 = (char) ('A' + random.nextInt(26));
        int numbers = random.nextInt(1000);
        return String.format("%c%c%03d", letter1, letter2, numbers);
    }
}
