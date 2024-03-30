package org.mae.twg.backend.dto.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на обратный звонок")
public class CallRequestDTO {
    @Schema(description = "Фио пользователя", example = "Романов Дмитрий Александрович")
    private String fullName;
    @Schema(description = "Телефон пользователя", example = "88005553535")
    private String phone;
    @Schema(description = "Id агентства", example = "1")
    private Long agency;
    @Schema(description = "Вопрос", example = "Как какать?")
    private String text;
}
