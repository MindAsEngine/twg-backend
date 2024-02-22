package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на фишку отеля")
public class PropertyRequestDTO implements Serializable {
    @Schema(description = "Название", example = "Какое-то название")
    private String title;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
}
