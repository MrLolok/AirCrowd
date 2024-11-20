package it.lorenzoangelino.aircrowd.flightschedule.spark;

import it.lorenzoangelino.aircrowd.common.spark.SparkExtractor;
import it.lorenzoangelino.aircrowd.flightschedule.converters.CSVConverter;
import lombok.RequiredArgsConstructor;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.util.Optional;

@RequiredArgsConstructor
public class FlightScheduleExtractor implements SparkExtractor {
    private final static CSVConverter CSV_CONVERTER = new CSVConverter();
    private final SparkSession spark;

    @Override
    public Dataset<Row> read(String path) {
        path = adjustFileFormat(path);
        return spark.read()
            .option("header", "true")
            .option("inferSchema", "true")
            .csv(path);
    }

    private String adjustFileFormat(String path) throws UnsupportedOperationException {
        if (path.endsWith(".csv"))
            return path;
        else if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
            String to = String.format("%s.csv", path.substring(0, path.lastIndexOf("\\.")));
            Optional<File> optional = CSV_CONVERTER.convert(path, to);
            if (optional.isEmpty())
                throw new RuntimeException();
            return optional.get().getAbsolutePath();
        } else
            throw new UnsupportedOperationException("Unable to read from this kind of file format.");
    }
}
