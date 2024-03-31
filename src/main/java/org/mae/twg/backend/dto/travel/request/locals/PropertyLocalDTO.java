package org.mae.twg.backend.dto.travel.request.locals;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на фишку отеля")
public class PropertyLocalDTO implements RequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    private String title;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
}
