package org.mae.twg.backend.dto.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на агенство")
public class AgencyRequestDTO {
    @Schema(description = "Название", example = "Какое-то название")
    String name;
    @Schema(description = "Описание", example = "Какое-то описание")
    String description;
    @Schema(description = "Контакты", example = "Какие-то контакты")
    String contacts;
    @Schema(description = "Адрес", example = "Какой-то адрес")
    String address;
}
