package org.mae.twg.backend.utils.excel;

import lombok.extern.log4j.Log4j2;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Log4j2
public class ExcelUtils {

    private Map<Integer, List<String>> readExcel(String fileLocation) throws IOException {
        log.debug("Start ExcelUtils.readExcel");
        Map<Integer, List<String>> data = new HashMap<>();
        try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
            Sheet sheet = wb.getFirstSheet();
            try (Stream<Row> rows = sheet.openStream()) {
                rows.forEach(r -> {
                    data.put(r.getRowNum(), new ArrayList<>());

                    for (Cell cell : r) {
                        if (cell == null) {
                            data.get(r.getRowNum()).add(null);
                            continue;
                        }
                        data.get(r.getRowNum()).add(cell.getRawValue());
                    }
                });
            }
        }
        data.remove(1);
        log.debug("End ExcelUtils.readExcel");
        return data;
    }

    private TourRow createTour(List<String> attributes) {
        log.debug("Start ExcelUtils.createTour");
        while(attributes.size() < 16) {
            attributes.add(null);
        }
        log.debug("Start ExcelUtils.createTour");
        return  TourRow.builder()
                .titleRU(attributes.get(0))
                .introductionRU(attributes.get(1))
                .descriptionRU(attributes.get(2))
                .additionalRU(attributes.get(3))
        
                .titleEN(attributes.get(4))
                .introductionEN(attributes.get(5))
                .descriptionEN(attributes.get(6))
                .additionalEN(attributes.get(7))
        
                .titleUZ(attributes.get(8))
                .introductionUZ(attributes.get(9))
                .descriptionUZ(attributes.get(10))
                .additionalUZ(attributes.get(11))

                .price(attributes.get(12) == null ? null : Long.valueOf(attributes.get(12)))
                .tourType(attributes.get(13))
                .duration(attributes.get(14) == null ? null : Integer.valueOf(attributes.get(14)))
                .build();
    }

    public List<TourRow> parseTourExcel(MultipartFile file) throws IOException {
        log.debug("Start ExcelUtils.parseTourExcel");
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        Map<Integer, List<String>> excelData = readExcel(tempFile.getAbsolutePath());
        List<TourRow> tourRows = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : excelData.entrySet()) {
            List<String> rowData = entry.getValue();
            TourRow tourRow = createTour(rowData);
            tourRows.add(tourRow);
        }
        tempFile.delete();
        log.debug("End ExcelUtils.parseTourExcel");
        return tourRows;
    }

    public String convertToursToExcel(List<TourRow> tourRows) throws IOException {
        log.debug("Start ExcelUtils.convertToursToExcel");

        String fileLocation = "C:\\Users\\SAPIPA\\Desktop\\fastexcel.xlsx";

        try (OutputStream os = new FileOutputStream(fileLocation); Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
            Worksheet sheet = wb.newWorksheet("Tours");
            String[] headers = {"titleRU", "introductionRU", "descriptionRU", "additionalRU",
                    "titleEN", "introductionEN", "descriptionEN", "additionalEN",
                    "titleUZ", "introductionUZ", "descriptionUZ", "additionalUZ",
                    "price", "tourType", "duration"};
            sheet.width(0, 25);
            sheet.width(1, 15);
            for (int i = 0; i < headers.length; ++i) {
                sheet.value(0, i, headers[i]);
            }
            int num = 0;
            for (TourRow tourRow : tourRows) {
                ++num;
                sheet.value(num, 0, tourRow.getTitleRU());
                sheet.value(num, 1, tourRow.getIntroductionRU());
                sheet.value(num, 2, tourRow.getDescriptionRU());
                sheet.value(num, 3, tourRow.getAdditionalRU());

                sheet.value(num, 4, tourRow.getTitleEN());
                sheet.value(num, 5, tourRow.getIntroductionEN());
                sheet.value(num, 6, tourRow.getDescriptionEN());
                sheet.value(num, 7, tourRow.getAdditionalEN());

                sheet.value(num, 8, tourRow.getTitleUZ());
                sheet.value(num, 9, tourRow.getIntroductionUZ());
                sheet.value(num, 10, tourRow.getDescriptionUZ());
                sheet.value(num, 11, tourRow.getAdditionalUZ());

                sheet.value(num, 12, tourRow.getPrice());
                sheet.value(num, 13, tourRow.getTourType());
                sheet.value(num, 14, tourRow.getDuration());
            }
            log.debug("End ExcelUtils.convertToursToExcel");
            return fileLocation;
        } finally {
            log.debug("Somethings wrong");
        }
    }
}