package org.mae.twg.backend.dto.travel.request.logic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

import java.time.LocalDate;

@Data
@Schema(description = "Запрос на обновление тура")
public class TourPeriodDTO implements RequestDTO {
    @Schema(description = "Дата начала тура", example = "2024-01-01")
    LocalDate startDate;
    @Schema(description = "Дата окончания тура", example = "2025-01-01")
    LocalDate endDate;
}
