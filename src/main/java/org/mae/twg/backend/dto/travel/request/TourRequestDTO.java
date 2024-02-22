package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;
import org.mae.twg.backend.models.travel.enums.TourType;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class TourRequestDTO implements RequestDTO {
@Schema(description = "Запрос на тур")
public class TourRequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    String title;
    @Schema(description = "Тип тура", example = "TOURISM")
    TourType type;
    @Schema(description = "Описание", example = "Какое-то описание")
    String description;
    @Schema(description = "Страна", example = "1")
    Long countryId;
    @Schema(description = "Агенство", example = "1")
    Long agencyId;
    @Schema(description = "Массив отелей", example = "1, 2, 3")
    List<Long> hotelIds;
    @Schema(description = "Массив курортов", example = "1, 2, 3")
    List<Long> resortIds;
    @Schema(description = "Горящий ли тур?", example = "true")
    Boolean isBurning;
    @Schema(description = "Кастомный ли тур", example = "true")
    Boolean isCustom;
    @Schema(description = "Активный ли тур?", example = "true")
    Boolean isActive;
    @Schema(description = "Дата начала тура", example = "2024-01-01")
    LocalDate startDate;
    @Schema(description = "Дата окончания тура", example = "2025-01-01")
    LocalDate endDate;
}
