package org.mae.twg.backend.utils.excel;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ExcelValidation {

    public void excelTourValidation(List<String> header) {
        if (!Objects.equals(header.get(0), "titleRU")) {
            throw new ValidationException("В файле отсутствует titleRU");
        }
        if (!Objects.equals(header.get(1), "introductionRU")) {
            throw new ValidationException("В файле отсутствует introductionRU");
        }
        if (!Objects.equals(header.get(2), "descriptionRU")) {
            throw new ValidationException("В файле отсутствует descriptionRU");
        }
        if (!Objects.equals(header.get(3), "additionalRU")) {
            throw new ValidationException("В файле отсутствует additionalRU");
        }
        if (!Objects.equals(header.get(4), "titleEN")) {
            throw new ValidationException("В файле отсутствует titleEN");
        }
        if (!Objects.equals(header.get(5), "introductionEN")) {
            throw new ValidationException("В файле отсутствует introductionEN");
        }
        if (!Objects.equals(header.get(6), "descriptionEN")) {
            throw new ValidationException("В файле отсутствует descriptionEN");
        }
        if (!Objects.equals(header.get(7), "additionalEN")) {
            throw new ValidationException("В файле отсутствует additionalEN");
        }
        if (!Objects.equals(header.get(8), "titleUZ")) {
            throw new ValidationException("В файле отсутствует titleUZ");
        }
        if (!Objects.equals(header.get(9), "introductionUZ")) {
            throw new ValidationException("В файле отсутствует introductionUZ");
        }
        if (!Objects.equals(header.get(10), "descriptionUZ")) {
            throw new ValidationException("В файле отсутствует descriptionUZ");
        }
        if (!Objects.equals(header.get(11), "additionalUZ")) {
            throw new ValidationException("В файле отсутствует additionalUZ");
        }
        if (!Objects.equals(header.get(12), "price")) {
            throw new ValidationException("В файле отсутствует price");
        }
        if (!Objects.equals(header.get(13), "tourType")) {
            throw new ValidationException("В файле отсутствует tourType");
        }
        if (!Objects.equals(header.get(14), "duration")) {
            throw new ValidationException("В файле отсутствует duration");
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
