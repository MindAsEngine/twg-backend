package org.mae.twg.backend.dto.travel.request.locals;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на тег")
public class TagLocalDTO implements RequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    private String name;
}
