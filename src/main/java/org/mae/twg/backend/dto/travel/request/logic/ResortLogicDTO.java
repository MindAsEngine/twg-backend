package org.mae.twg.backend.dto.travel.request.logic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на курорт")
public class ResortLogicDTO implements RequestDTO {
    @Schema(description = "Id страны тура", example = "1")
    private Long countryId;
}
