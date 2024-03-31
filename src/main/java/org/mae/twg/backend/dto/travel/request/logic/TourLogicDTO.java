package org.mae.twg.backend.dto.travel.request.logic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;
import org.mae.twg.backend.models.travel.enums.TourType;

import java.util.List;

@Data
@Schema(description = "Запрос на обновление тура")
public class TourLogicDTO implements RequestDTO {
    @Schema(description = "Тип тура", example = "Какой-то тип")
    TourType type;
    @Schema(description = "Цена в доллар центах", example = "300000")
    Long price;
    @Schema(description = "Id страны", example = "1")
    Long countryId;
    @Schema(description = "Id больницы", example = "1")
    Long hospitalId;
    @Schema(description = "Длительность тура (дней)", example = "4")
    Integer duration;
    @Schema(description = "Активный ли тур?", example = "true")
    Boolean isActive;
    @Schema(description = "Список id тегов", example = "[1,2,3]")
    List<Long> tagIds;
    @Schema(description = "Список id отелей", example = "[1,2,3]")
    List<Long> hotelIds;
}
