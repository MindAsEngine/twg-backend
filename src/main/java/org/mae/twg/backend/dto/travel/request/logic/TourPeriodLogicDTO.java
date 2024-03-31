package org.mae.twg.backend.dto.travel.request.logic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

import java.time.LocalDate;

@Data
@Schema(description = "Запрос на период тура")
public class TourPeriodLogicDTO implements RequestDTO {
    @Schema(description = "Дата начала тура", example = "2024-06-20")
    private LocalDate startDate;
    @Schema(description = "Дата конца тура", example = "2024-06-20")
    private LocalDate endDate;
}
