package org.mae.twg.backend.utils.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.exceptions.CSVException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
public class CSVUtils {

    private InputStreamResource outStreamToInputResource(ByteArrayOutputStream out) {
        return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
    }

    public List<TourRow> parseTourCSV(MultipartFile file) {
        log.debug("Start run CSVUtils.parseTourCSV");
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }
        if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
            throw new ValidationException("File is not .csv");
        }
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<TourRow> csvToBean = new CsvToBeanBuilder<TourRow>(reader)
                    .withType(TourRow.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSeparator(';')
                    .build();

            log.debug("End run CSVUtils.parseTourCSV");
            return csvToBean.parse();
        } catch (Exception exception) {
            log.warn("Tour .csv file parse error: " + exception);
            throw new CSVException("Something went wrong while parsing .csv file\n" + exception);
        }
    }

    public InputStreamResource convertToursToCSV(List<TourRow> tourRows) {
        log.debug("Start run CSVUtils.convertToursToCSV");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                Writer writer = new BufferedWriter(new OutputStreamWriter(out))) {
            StatefulBeanToCsv<TourRow> beanToCsv = new StatefulBeanToCsvBuilder<TourRow>(writer)
                    .withQuotechar('\'')
                    .withSeparator(';')
                    .build();

            beanToCsv.write(tourRows);
            log.debug("End run CSVUtils.convertToursToCSV");
            return outStreamToInputResource(out);
        } catch (Exception exception) {
            log.warn("Tour .csv file write error: " + exception);
            throw new CSVException("Something went wrong while writing .csv file\n" + exception);
        }
    }

}
