package it.lorenzoangelino.aircrowd.flightschedule.readers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

@Slf4j
public class XLSXFileReader implements FileReader<String[]> {

    @Override
    public List<String[]> read(String path) {
        List<String[]> data = new ArrayList<>();
        try (FileInputStream is = new FileInputStream(path)) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                int lastCellNum = row.getLastCellNum();
                String[] rowData = new String[lastCellNum];
                for (int i = 0; i < lastCellNum; i++) {
                    Cell cell = row.getCell(i);
                    rowData[i] = cell == null ? "" : formatter.formatCellValue(cell);
                }
                data.add(rowData);
            }
            workbook.close();
        } catch (IOException e) {
            log.warn("IOException encountered while reading file {}", path, e);
        }
        return data;
    }
}
