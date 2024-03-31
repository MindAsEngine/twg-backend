package org.mae.twg.backend.dto.travel.request.locals;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на локализация точки интереса")
public class SightLocalDTO implements RequestDTO {
    @Schema(description = "Имя", example = "Какое-то название")
    private String name;
    @Schema(description = "Введение", example = "Какое-то описание")
    private String introduction;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
    @Schema(description = "Адрес", example = "Какой-то адрес")
    private String address;
}
