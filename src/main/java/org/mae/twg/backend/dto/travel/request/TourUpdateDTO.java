package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.models.travel.enums.TourType;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на обновление тура")
public class TourUpdateDTO {
    @Schema(description = "Тип тура", example = "Какой-то тип")
    TourType type;
    @Schema(description = "Страна", example = "1")
    Long countryId;
    @Schema(description = "Агенство", example = "1")
    Long agencyId;
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
