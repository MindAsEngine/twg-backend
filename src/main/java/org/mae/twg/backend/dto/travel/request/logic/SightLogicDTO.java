package org.mae.twg.backend.dto.travel.request.logic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на логику точки интереса")
public class SightLogicDTO implements RequestDTO {
    @Schema(description = "Id типа точки интереса", example = "1")
    private Long sightTypeId;
}
