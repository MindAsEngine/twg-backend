package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;
import org.mae.twg.backend.models.travel.enums.Stars;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на отеля")
public class HotelRequestDTO implements RequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    private String name;
    @Schema(description = "Город", example = "Какой-то город")
    private String city;
    @Schema(description = "Звезды", example = "Без звезд")
    private Stars stars;
    @Schema(description = "Введение", example = "Какое-то описание")
    private String introduction;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
    @Schema(description = "Адрес", example = "Какой-то адрес")
    private String address;
    @Schema(description = "Массив свойств отеля", example = "1, 2, 3")
    private List<Long> propertyIds;
    @Schema(description = "Массив точек интереса", example = "1, 2, 3")
    private List<Long> sightIds;
}
