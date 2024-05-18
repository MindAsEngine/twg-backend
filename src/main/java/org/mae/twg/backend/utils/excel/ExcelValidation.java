package org.mae.twg.backend.utils.excel;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ExcelValidation {

    public void excelTourValidation(List<String> header) {
        StringBuilder exceptionMsg = new StringBuilder();
        if (header.isEmpty() || header.size() != 15) {
            throw new ValidationException("В файле неверное число заголовков");
        }
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
        if (!exceptionMsg.isEmpty()) {
            throw new ValidationException(exceptionMsg.toString());
        }
    }
    public void excelHotelValidation(List<String> header) {
        if (header.isEmpty() || header.size() != 18) {
            throw new ValidationException("В файле неверное число заголовков\n");
        }
        StringBuilder exceptionMsg = new StringBuilder();
        if (!Objects.equals(header.get(0), "nameRU")) {
            exceptionMsg.append("В файле отсутствует nameRU\n");
        }
        if (!Objects.equals(header.get(1), "introductionRU")) {
            exceptionMsg.append("В файле отсутствует introductionRU\n");
        }
        if (!Objects.equals(header.get(2), "descriptionRU")) {
            exceptionMsg.append("В файле отсутствует descriptionRU\n");
        }
        if (!Objects.equals(header.get(3), "cityRU")) {
            exceptionMsg.append("В файле отсутствует cityRU\n");
        }
        if (!Objects.equals(header.get(4), "addressRU")) {
            exceptionMsg.append("В файле отсутствует addressRU\n");
        }
        if (!Objects.equals(header.get(5), "nameEN")) {
            exceptionMsg.append("В файле отсутствует nameEN\n");
        }
        if (!Objects.equals(header.get(6), "introductionEN")) {
            exceptionMsg.append("В файле отсутствует introductionEN\n");
        }
        if (!Objects.equals(header.get(7), "descriptionEN")) {
            exceptionMsg.append("В файле отсутствует descriptionEN\n");
        }
        if (!Objects.equals(header.get(8), "cityEN")) {
            exceptionMsg.append("В файле отсутствует cityEN\n");
        }
        if (!Objects.equals(header.get(9), "addressEN")) {
            exceptionMsg.append("В файле отсутствует addressEN\n");
        }
        if (!Objects.equals(header.get(10), "nameUZ")) {
            exceptionMsg.append("В файле отсутствует nameUZ\n");
        }
        if (!Objects.equals(header.get(11), "introductionUZ")) {
            exceptionMsg.append("В файле отсутствует introductionUZ\n");
        }
        if (!Objects.equals(header.get(12), "descriptionUZ")) {
            exceptionMsg.append("В файле отсутствует descriptionUZ\n");
        }
        if (!Objects.equals(header.get(13), "cityUZ")) {
            exceptionMsg.append("В файле отсутствует cityUZ\n");
        }
        if (!Objects.equals(header.get(14), "addressUZ")) {
            exceptionMsg.append("В файле отсутствует addressUZ\n");
        }
        if (!Objects.equals(header.get(15), "stars")) {
            exceptionMsg.append("В файле отсутствует stars\n");
        }
        if (!Objects.equals(header.get(16), "latitude")) {
            exceptionMsg.append("В файле отсутствует latitude\n");
        }
        if (!Objects.equals(header.get(17), "longitude")) {
            exceptionMsg.append("В файле отсутствует longitude\n");
        }
        if (!exceptionMsg.isEmpty()) {
            throw new ValidationException(exceptionMsg.toString());
        }
    }
    public void excelHospitalValidation(List<String> header) {
        if (header.isEmpty() || header.size() != 17) {
            throw new ValidationException("В файле неверное число заголовков\n");
        }
        StringBuilder exceptionMsg = new StringBuilder();
        if (!Objects.equals(header.get(0), "nameRU")) {
            exceptionMsg.append("В файле отсутствует nameRU\n");
        }
        if (!Objects.equals(header.get(1), "introductionRU")) {
            exceptionMsg.append("В файле отсутствует introductionRU\n");
        }
        if (!Objects.equals(header.get(2), "descriptionRU")) {
            exceptionMsg.append("В файле отсутствует descriptionRU\n");
        }
        if (!Objects.equals(header.get(3), "cityRU")) {
            exceptionMsg.append("В файле отсутствует cityRU\n");
        }
        if (!Objects.equals(header.get(4), "addressRU")) {
            exceptionMsg.append("В файле отсутствует addressRU\n");
        }
        if (!Objects.equals(header.get(5), "nameEN")) {
            exceptionMsg.append("В файле отсутствует nameEN\n");
        }
        if (!Objects.equals(header.get(6), "introductionEN")) {
            exceptionMsg.append("В файле отсутствует introductionEN\n");
        }
        if (!Objects.equals(header.get(7), "descriptionEN")) {
            exceptionMsg.append("В файле отсутствует descriptionEN\n");
        }
        if (!Objects.equals(header.get(8), "cityEN")) {
            exceptionMsg.append("В файле отсутствует cityEN\n");
        }
        if (!Objects.equals(header.get(9), "addressEN")) {
            exceptionMsg.append("В файле отсутствует addressEN\n");
        }
        if (!Objects.equals(header.get(10), "nameUZ")) {
            exceptionMsg.append("В файле отсутствует nameUZ\n");
        }
        if (!Objects.equals(header.get(11), "introductionUZ")) {
            exceptionMsg.append("В файле отсутствует introductionUZ\n");
        }
        if (!Objects.equals(header.get(12), "descriptionUZ")) {
            exceptionMsg.append("В файле отсутствует descriptionUZ\n");
        }
        if (!Objects.equals(header.get(13), "cityUZ")) {
            exceptionMsg.append("В файле отсутствует cityUZ\n");
        }
        if (!Objects.equals(header.get(14), "addressUZ")) {
            exceptionMsg.append("В файле отсутствует addressUZ\n");
        }
        if (!Objects.equals(header.get(15), "latitude")) {
            exceptionMsg.append("В файле отсутствует latitude\n");
        }
        if (!Objects.equals(header.get(16), "longitude")) {
            exceptionMsg.append("В файле отсутствует longitude\n");
        }
        if (!exceptionMsg.isEmpty()) {
            throw new ValidationException(exceptionMsg.toString());
        }
    }
    public void excelSightValidation(List<String> header) {
        if (header.isEmpty() || header.size() != 14) {
            throw new ValidationException("В файле неверное число заголовков\n");
        }
        StringBuilder exceptionMsg = new StringBuilder();
        if (!Objects.equals(header.get(0), "nameRU")) {
            exceptionMsg.append("В файле отсутствует nameRU\n");
        }
        if (!Objects.equals(header.get(1), "introductionRU")) {
            exceptionMsg.append("В файле отсутствует introductionRU\n");
        }
        if (!Objects.equals(header.get(2), "descriptionRU")) {
            exceptionMsg.append("В файле отсутствует descriptionRU\n");
        }
        if (!Objects.equals(header.get(3), "addressRU")) {
            exceptionMsg.append("В файле отсутствует addressRU\n");
        }
        if (!Objects.equals(header.get(4), "nameEN")) {
            exceptionMsg.append("В файле отсутствует nameEN\n");
        }
        if (!Objects.equals(header.get(5), "introductionEN")) {
            exceptionMsg.append("В файле отсутствует introductionEN\n");
        }
        if (!Objects.equals(header.get(6), "descriptionEN")) {
            exceptionMsg.append("В файле отсутствует descriptionEN\n");
        }
        if (!Objects.equals(header.get(7), "addressEN")) {
            exceptionMsg.append("В файле отсутствует addressEN\n");
        }
        if (!Objects.equals(header.get(8), "nameUZ")) {
            exceptionMsg.append("В файле отсутствует nameUZ\n");
        }
        if (!Objects.equals(header.get(9), "introductionUZ")) {
            exceptionMsg.append("В файле отсутствует introductionUZ\n");
        }
        if (!Objects.equals(header.get(10), "descriptionUZ")) {
            exceptionMsg.append("В файле отсутствует descriptionUZ\n");
        }
        if (!Objects.equals(header.get(11), "addressUZ")) {
            exceptionMsg.append("В файле отсутствует addressUZ\n");
        }
        if (!Objects.equals(header.get(12), "latitude")) {
            exceptionMsg.append("В файле отсутствует latitude\n");
        }
        if (!Objects.equals(header.get(13), "longitude")) {
            exceptionMsg.append("В файле отсутствует longitude\n");
        }
        if (!exceptionMsg.isEmpty()) {
            throw new ValidationException(exceptionMsg.toString());
        }
    }
}
