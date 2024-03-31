package org.mae.twg.backend.dto.travel.request.logic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на тег")
public class TagLogicDTO implements RequestDTO {
    @Schema(description = "Иконка", example = "wireless")
    private String icon;
}
