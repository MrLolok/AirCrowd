package it.lorenzoangelino.aircrowd.flightschedule.converters;

import it.lorenzoangelino.aircrowd.flightschedule.readers.XLSXFileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CSVConverter implements ExtensionConverter {
    private static final XLSXFileReader XLSX_FILE_READER = new XLSXFileReader();
    private static final Logger LOGGER = LogManager.getLogger(CSVConverter.class);

    @Override
    public Optional<File> convert(String from, String to) {
        File file = null;
        boolean valid = validate(from, to);
        if (valid) {
            List<String[]> data = XLSX_FILE_READER.read(from);
            try {
                file = writeCSV(data, to);
            } catch (IOException e) {
                LOGGER.warn("Unable to write CSV file: {} -> {}", from, to, e);
            }
        }
        return Optional.ofNullable(file);
    }

    private File writeCSV(List<String[]> data, String path) throws IOException {
        try (FileWriter writer = new FileWriter(path);
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            for (String[] row : data) printer.printRecord((Object[]) row);
            printer.flush();
        }
        return new File(path);
    }

    private boolean validate(String from, String to) {
        if (!(from.endsWith(".xls") || from.endsWith(".xlsx")))
            throw new UnsupportedOperationException(
                    "Conversion from other file except Excel tables are not supported.");
        if (!to.endsWith(".csv"))
            throw new IllegalArgumentException("Target CSV file must end with the proper extension (.csv).");
        return true;
    }
}
