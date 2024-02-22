package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на курорт")
public class ResortRequestDTO implements Serializable {
    @Schema(description = "Название", example = "Какое-то название")
    private String name;
    @Schema(description = "Описание", example = "Какое-то описание")
    private String description;
}
