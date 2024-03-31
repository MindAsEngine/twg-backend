package org.mae.twg.backend.dto.travel.request.locals;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на обновление локали тура")
public class TourLocalDTO implements RequestDTO {
    @Schema(description = "Название", example = "Какой-то название")
    String title;
    @Schema(description = "Введение", example = "Какое-то описание")
    String introduction;
    @Schema(description = "Описание", example = "Какое-то описание")
    String description;
    @Schema(description = "Дополнительная информация", example = "Какое-то описание")
    String additional;
}
