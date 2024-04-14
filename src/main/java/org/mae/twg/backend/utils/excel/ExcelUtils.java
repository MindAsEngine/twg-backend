package org.mae.twg.backend.utils.excel;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.mae.twg.backend.utils.excel.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Component
@Log4j2
@RequiredArgsConstructor
public class ExcelUtils {
    private final ExcelValidation excelValidation;

    @Value("${upload.excel}")
    private String parent_path;

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
        log.debug("End ExcelUtils.readExcel");
        return data;
    }

    private TourRow createTourRow(List<String> attributes) {
        log.debug("Start ExcelUtils.createTour");
        while(attributes.size() < 16) {
            attributes.add(null);
        }
        log.debug("End ExcelUtils.createTour");
        try {
            return TourRow.builder()
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
        } catch (Exception e) {
            log.error("Вы не передали число, где это необходимо");
            throw new ValidationException("Вы не передали число, где это необходимо");
        }
    }

    private HotelRow createHotelRow(List<String> attributes) {
        log.debug("Start ExcelUtils.createHotelRow");
        while(attributes.size() < 19) {
            attributes.add(null);
        }
        log.debug("End ExcelUtils.createHotelRow");
        try {
            return HotelRow.builder()
                    .nameRU(attributes.get(0))
                    .introductionRU(attributes.get(1))
                    .descriptionRU(attributes.get(2))
                    .cityRU(attributes.get(3))
                    .addressRU(attributes.get(4))

                    .nameEN(attributes.get(5))
                    .introductionEN(attributes.get(6))
                    .descriptionEN(attributes.get(7))
                    .cityEN(attributes.get(8))
                    .addressEN(attributes.get(9))

                    .nameUZ(attributes.get(10))
                    .introductionUZ(attributes.get(11))
                    .descriptionUZ(attributes.get(12))
                    .cityUZ(attributes.get(13))
                    .addressUZ(attributes.get(14))

                    .stars(attributes.get(15))
                    .latitude(attributes.get(16) == null ? null : Double.valueOf(attributes.get(16)))
                    .longitude(attributes.get(17) == null ? null : Double.valueOf(attributes.get(17)))
                    .build();
        } catch (Exception e) {
            log.error("Вы не передали число, где это необходимо");
            throw new ValidationException("Вы не передали число, где это необходимо");
        }
    }

    private HospitalRow createHospitalRow(List<String> attributes) {
        log.debug("Start ExcelUtils.createHospitalRow");
        while(attributes.size() < 18) {
            attributes.add(null);
        }
        log.debug("End ExcelUtils.createHospitalRow");
        try {
            return HospitalRow.builder()
                    .nameRU(attributes.get(0))
                    .introductionRU(attributes.get(1))
                    .descriptionRU(attributes.get(2))
                    .cityRU(attributes.get(3))
                    .addressRU(attributes.get(4))

                    .nameEN(attributes.get(5))
                    .introductionEN(attributes.get(6))
                    .descriptionEN(attributes.get(7))
                    .cityEN(attributes.get(8))
                    .addressEN(attributes.get(9))

                    .nameUZ(attributes.get(10))
                    .introductionUZ(attributes.get(11))
                    .descriptionUZ(attributes.get(12))
                    .cityUZ(attributes.get(13))
                    .addressUZ(attributes.get(14))

                    .latitude(attributes.get(15) == null ? null : Double.valueOf(attributes.get(15)))
                    .longitude(attributes.get(16) == null ? null : Double.valueOf(attributes.get(16)))
                    .build();
        } catch (Exception e) {
            log.error("Вы не передали число, где это необходимо");
            throw new ValidationException("Вы не передали число, где это необходимо");
        }
    }

    private SightRow createSightRow(List<String> attributes) {
        log.debug("Start ExcelUtils.createSightRow");
        while(attributes.size() < 15) {
            attributes.add(null);
        }
        log.debug("End ExcelUtils.createSightRow");
        try {
            return SightRow.builder()
                    .nameRU(attributes.get(0))
                    .introductionRU(attributes.get(1))
                    .descriptionRU(attributes.get(2))
                    .addressRU(attributes.get(3))

                    .nameEN(attributes.get(4))
                    .introductionEN(attributes.get(5))
                    .descriptionEN(attributes.get(6))
                    .addressEN(attributes.get(7))

                    .nameUZ(attributes.get(8))
                    .introductionUZ(attributes.get(9))
                    .descriptionUZ(attributes.get(10))
                    .addressUZ(attributes.get(11))

                    .latitude(attributes.get(12) == null ? null : Double.valueOf(attributes.get(12)))
                    .longitude(attributes.get(13) == null ? null : Double.valueOf(attributes.get(13)))
                    .build();
        } catch (Exception e) {
            log.error("Вы не передали число, где это необходимо");
            throw new ValidationException("Вы не передали число, где это необходимо");
        }
    }

    public List<TourRow> parseTourExcel(MultipartFile file) throws IOException {
        log.debug("Start ExcelUtils.parseTourExcel");
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        Map<Integer, List<String>> excelData = readExcel(tempFile.getAbsolutePath());
        excelValidation.excelTourValidation(excelData.get(1));
        excelData.remove(1);
        List<TourRow> tourRows = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : excelData.entrySet()) {
            List<String> rowData = entry.getValue();
            TourRow tourRow = createTourRow(rowData);
            tourRows.add(tourRow);
        }
        tempFile.delete();
        log.debug("End ExcelUtils.parseTourExcel");
        return tourRows;
    }

    public List<HotelRow> parseHotelExcel(MultipartFile file) throws IOException {
        log.debug("Start ExcelUtils.parseHotelExcel");
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        Map<Integer, List<String>> excelData = readExcel(tempFile.getAbsolutePath());
        excelValidation.excelHotelValidation(excelData.get(1));
        excelData.remove(1);
        List<HotelRow> hotelRows = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : excelData.entrySet()) {
            List<String> rowData = entry.getValue();
            HotelRow hotelRow = createHotelRow(rowData);
            hotelRows.add(hotelRow);
        }
        tempFile.delete();
        log.debug("End ExcelUtils.parseHotelExcel");
        return hotelRows;
    }

    public List<HospitalRow> parseHospitalExcel(MultipartFile file) throws IOException {
        log.debug("Start ExcelUtils.parseHospitalExcel");
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        Map<Integer, List<String>> excelData = readExcel(tempFile.getAbsolutePath());
        excelValidation.excelHospitalValidation(excelData.get(1));
        excelData.remove(1);
        List<HospitalRow> hospitalRows = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : excelData.entrySet()) {
            List<String> rowData = entry.getValue();
            HospitalRow hospitalRow = createHospitalRow(rowData);
            hospitalRows.add(hospitalRow);
        }
        tempFile.delete();
        log.debug("End ExcelUtils.parseHospitalExcel");
        return hospitalRows;
    }

    public List<SightRow> parseSightExcel(MultipartFile file) throws IOException {
        log.debug("Start ExcelUtils.parseSightExcel");
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);
        Map<Integer, List<String>> excelData = readExcel(tempFile.getAbsolutePath());
        excelValidation.excelSightValidation(excelData.get(1));
        excelData.remove(1);
        List<SightRow> sightRows = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : excelData.entrySet()) {
            List<String> rowData = entry.getValue();
            SightRow sightRow = createSightRow(rowData);
            sightRows.add(sightRow);
        }
        tempFile.delete();
        log.debug("End ExcelUtils.parseSightExcel");
        return sightRows;
    }

    public String convertToursToExcel(List<TourRow> tourRows) throws IOException {
        log.debug("Start ExcelUtils.convertToursToExcel");

        File currDir = new File(parent_path);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + "/tours.xlsx";

        try (OutputStream os = Files.newOutputStream(Paths.get(fileLocation)); Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
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

    public String convertHotelsToExcel(List<HotelRow> hotelRows) throws IOException {
        log.debug("Start ExcelUtils.convertHotelsToExcel");

        File currDir = new File(parent_path);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + "/hotels.xlsx";

        try (OutputStream os = Files.newOutputStream(Paths.get(fileLocation)); Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
            Worksheet sheet = wb.newWorksheet("Hotels");
            String[] headers = {"nameRU", "introductionRU", "descriptionRU", "cityRU", "addressRU",
                    "nameEN", "introductionEN", "descriptionEN", "cityEN", "addressEN",
                    "nameUZ", "introductionUZ", "descriptionUZ", "cityUZ", "addressUZ",
                    "stars", "latitude", "longitude"};
            sheet.width(0, 25);
            sheet.width(1, 15);
            for (int i = 0; i < headers.length; ++i) {
                sheet.value(0, i, headers[i]);
            }
            int num = 0;
            for (HotelRow hotelRow : hotelRows) {
                ++num;
                sheet.value(num, 0, hotelRow.getNameRU());
                sheet.value(num, 1, hotelRow.getIntroductionRU());
                sheet.value(num, 2, hotelRow.getDescriptionRU());
                sheet.value(num, 3, hotelRow.getCityRU());
                sheet.value(num, 4, hotelRow.getAddressRU());

                sheet.value(num, 5, hotelRow.getNameEN());
                sheet.value(num, 6, hotelRow.getIntroductionEN());
                sheet.value(num, 7, hotelRow.getDescriptionEN());
                sheet.value(num, 8, hotelRow.getCityEN());
                sheet.value(num, 9, hotelRow.getAddressEN());

                sheet.value(num, 10, hotelRow.getNameUZ());
                sheet.value(num, 11, hotelRow.getIntroductionUZ());
                sheet.value(num, 12, hotelRow.getDescriptionUZ());
                sheet.value(num, 13, hotelRow.getCityUZ());
                sheet.value(num, 14, hotelRow.getAddressUZ());

                sheet.value(num, 15, hotelRow.getStars());
                sheet.value(num, 16, hotelRow.getLatitude());
                sheet.value(num, 17, hotelRow.getLongitude());
            }
            log.debug("End ExcelUtils.convertHotelsToExcel");
            return fileLocation;
        } finally {
            log.debug("Somethings wrong");
        }
    }

    public String convertHospitalsToExcel(List<HospitalRow> hospitalRows) throws IOException {
        log.debug("Start ExcelUtils.convertHospitalsToExcel");

        File currDir = new File(parent_path);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + "/hospitals.xlsx";

        try (OutputStream os = Files.newOutputStream(Paths.get(fileLocation)); Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
            Worksheet sheet = wb.newWorksheet("Hospitals");
            String[] headers = {"nameRU", "introductionRU", "descriptionRU", "cityRU", "addressRU",
                    "nameEN", "introductionEN", "descriptionEN", "cityEN", "addressEN",
                    "nameUZ", "introductionUZ", "descriptionUZ", "cityUZ", "addressUZ",
                    "latitude", "longitude"};
            sheet.width(0, 25);
            sheet.width(1, 15);
            for (int i = 0; i < headers.length; ++i) {
                sheet.value(0, i, headers[i]);
            }
            int num = 0;
            for (HospitalRow hospitalRow : hospitalRows) {
                ++num;
                sheet.value(num, 0, hospitalRow.getNameRU());
                sheet.value(num, 1, hospitalRow.getIntroductionRU());
                sheet.value(num, 2, hospitalRow.getDescriptionRU());
                sheet.value(num, 3, hospitalRow.getCityRU());
                sheet.value(num, 4, hospitalRow.getAddressRU());

                sheet.value(num, 5, hospitalRow.getNameEN());
                sheet.value(num, 6, hospitalRow.getIntroductionEN());
                sheet.value(num, 7, hospitalRow.getDescriptionEN());
                sheet.value(num, 8, hospitalRow.getCityEN());
                sheet.value(num, 9, hospitalRow.getAddressEN());

                sheet.value(num, 10, hospitalRow.getNameUZ());
                sheet.value(num, 11, hospitalRow.getIntroductionUZ());
                sheet.value(num, 12, hospitalRow.getDescriptionUZ());
                sheet.value(num, 13, hospitalRow.getCityUZ());
                sheet.value(num, 14, hospitalRow.getAddressUZ());

                sheet.value(num, 15, hospitalRow.getLatitude());
                sheet.value(num, 16, hospitalRow.getLongitude());
            }
            log.debug("End ExcelUtils.convertHospitalsToExcel");
            return fileLocation;
        } finally {
            log.debug("Somethings wrong");
        }
    }
    public String convertSightsToExcel(List<SightRow> sightRows) throws IOException {
        log.debug("Start ExcelUtils.convertSightsToExcel");

        File currDir = new File(parent_path);
        String path = currDir.getAbsolutePath();
        String fileLocation = path + "/sights.xlsx";

        try (OutputStream os = Files.newOutputStream(Paths.get(fileLocation)); Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
            Worksheet sheet = wb.newWorksheet("Sights");
            String[] headers = {"nameRU", "introductionRU", "descriptionRU", "addressRU",
                    "nameEN", "introductionEN", "descriptionEN", "addressEN",
                    "nameUZ", "introductionUZ", "descriptionUZ", "addressUZ",
                    "latitude", "longitude"};
            sheet.width(0, 25);
            sheet.width(1, 15);
            for (int i = 0; i < headers.length; ++i) {
                sheet.value(0, i, headers[i]);
            }
            int num = 0;
            for (SightRow sightRow : sightRows) {
                ++num;
                sheet.value(num, 0, sightRow.getNameRU());
                sheet.value(num, 1, sightRow.getIntroductionRU());
                sheet.value(num, 2, sightRow.getDescriptionRU());
                sheet.value(num, 3, sightRow.getAddressRU());

                sheet.value(num, 4, sightRow.getNameEN());
                sheet.value(num, 5, sightRow.getIntroductionEN());
                sheet.value(num, 6, sightRow.getDescriptionEN());
                sheet.value(num, 7, sightRow.getAddressEN());

                sheet.value(num, 8, sightRow.getNameUZ());
                sheet.value(num, 9, sightRow.getIntroductionUZ());
                sheet.value(num, 10, sightRow.getDescriptionUZ());
                sheet.value(num, 11, sightRow.getAddressUZ());

                sheet.value(num, 12, sightRow.getLatitude());
                sheet.value(num, 13, sightRow.getLongitude());
            }
            log.debug("End ExcelUtils.convertSightsToExcel");
            return fileLocation;
        } finally {
            log.debug("Somethings wrong");
        }
    }
}
