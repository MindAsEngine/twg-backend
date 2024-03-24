package org.mae.twg.backend.dto.news;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на локаль новости")
public class NewsLocalRequestDTO implements RequestDTO {
    @Schema(description = "Название", example = "Праздник")
    private String title;
    @Schema(description = "Описание", example = "16.03 произошло моё день рождения")
    private String description;
}
