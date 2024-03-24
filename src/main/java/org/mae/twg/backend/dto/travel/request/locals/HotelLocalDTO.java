package org.mae.twg.backend.dto.travel.request.locals;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на локаль отеля")
public class HotelLocalDTO implements RequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    private String name;
    @Schema(description = "Город", example = "Какой-то город")
    private String city;
    @Schema(description = "Введение", example = "Какое-то описание")
    private String introduction;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
    @Schema(description = "Адрес", example = "Какой-то адрес")
    private String address;
}
