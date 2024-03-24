package org.mae.twg.backend.dto.travel.request.logic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;
import org.mae.twg.backend.models.travel.enums.Stars;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на отеля")
public class HotelLogicDTO implements RequestDTO {
    @Schema(description = "Звезды", example = "NULL")
    private Stars stars;
    @Schema(description = "Массив свойств отеля", example = "1, 2, 3")
    private List<Long> propertyIds;
    @Schema(description = "Массив точек интереса", example = "1, 2, 3")
    private List<Long> sightIds;
}
