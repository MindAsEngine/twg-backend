package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на локаль отеля")
public class HotelLocalRequestDTO implements Serializable {
    @Schema(description = "Название", example = "Какое-то название")
    private String name;
    @Schema(description = "Город", example = "Какой-то город")
    private String city;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
    @Schema(description = "Адрес", example = "Какой-то адрес")
    private String address;
    @Schema(description = "Локализация", example = "RU")
    private Localization localization;
}
