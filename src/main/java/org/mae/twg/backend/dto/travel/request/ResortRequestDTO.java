package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на курорт")
public class ResortRequestDTO implements RequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    private String name;
}
