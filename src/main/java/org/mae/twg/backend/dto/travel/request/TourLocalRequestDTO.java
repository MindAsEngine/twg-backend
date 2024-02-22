package org.mae.twg.backend.dto.travel.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на обновление локали тура")
public class TourLocalRequestDTO implements Serializable {
    @Schema(description = "Название", example = "Какой-то название")
    String title;
    @Schema(description = "Описание", example = "Какой-то описание")
    String description;
}
