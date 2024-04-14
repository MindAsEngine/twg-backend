package org.mae.twg.backend.utils.excel;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ExcelValidation {

    public void excelTourValidation(List<String> header) {
        StringBuilder exceptionMsg = new StringBuilder();
        if (!Objects.equals(header.get(0), "titleRU")) {
            exceptionMsg.append("В файле отсутствует titleRU\n");
        }
        if (!Objects.equals(header.get(1), "introductionRU")) {
            exceptionMsg.append("В файле отсутствует introductionRU\n");
        }
        if (!Objects.equals(header.get(2), "descriptionRU")) {
            exceptionMsg.append("В файле отсутствует descriptionRU\n");
        }
        if (!Objects.equals(header.get(3), "additionalRU")) {
            exceptionMsg.append("В файле отсутствует additionalRU\n");
        }
        if (!Objects.equals(header.get(4), "titleEN")) {
            exceptionMsg.append("В файле отсутствует titleEN\n");
        }
        if (!Objects.equals(header.get(5), "introductionEN")) {
            exceptionMsg.append("В файле отсутствует introductionEN\n");
        }
        if (!Objects.equals(header.get(6), "descriptionEN")) {
            exceptionMsg.append("В файле отсутствует descriptionEN\n");
        }
        if (!Objects.equals(header.get(7), "additionalEN")) {
            exceptionMsg.append("В файле отсутствует additionalEN\n");
        }
        if (!Objects.equals(header.get(8), "titleUZ")) {
            exceptionMsg.append("В файле отсутствует titleUZ\n");
        }
        if (!Objects.equals(header.get(9), "introductionUZ")) {
            exceptionMsg.append("В файле отсутствует introductionUZ\n");
        }
        if (!Objects.equals(header.get(10), "descriptionUZ")) {
            exceptionMsg.append("В файле отсутствует descriptionUZ\n");
        }
        if (!Objects.equals(header.get(11), "additionalUZ")) {
            exceptionMsg.append("В файле отсутствует additionalUZ\n");
        }
        if (!Objects.equals(header.get(12), "price")) {
            exceptionMsg.append("В файле отсутствует price\n");
        }
        if (!Objects.equals(header.get(13), "tourType")) {
            exceptionMsg.append("В файле отсутствует tourType\n");
        }
        if (!Objects.equals(header.get(14), "duration")) {
            exceptionMsg.append("В файле отсутствует duration\n");
        }
        if (exceptionMsg.isEmpty()) {
            throw new ValidationException(exceptionMsg.toString());
        }
    }
    public void excelHotelValidation(List<String> header) {
    }
    public void excelHospitalValidation(List<String> header) {
    }

    public void excelSightValidation(List<String> header) {
    }

    public void excelResortValidation(List<String> header) {
    }

}
