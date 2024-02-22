package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на страну")
public class CountryRequestDTO implements RequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    private String name;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
}
