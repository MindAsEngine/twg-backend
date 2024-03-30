package org.mae.twg.backend.dto.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.models.business.CallRequest;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Заявка на звонок")
public class CallReqResponseDTO {
    @Schema(description = "id заявки", example = "1")
    private Long id;
    @Schema(description = "Фио пользователя", example = "Романов Дмитрий Александрович")
    private String fullName;
    @Schema(description = "Телефон пользователя", example = "88005553535")
    private String phone;
    @Schema(description = "Id агентства", example = "1")
    private Long agency;
    @Schema(description = "Вопрос", example = "Как какать?")
    private String text;
    @Schema(description = "Дата создания", example = "01.01.01")
    private LocalDateTime createdAt;

    public CallReqResponseDTO(CallRequest callRequest) {
        this.id = callRequest.getId();
        this.fullName = callRequest.getUser();
        this.phone = callRequest.getNumber();
        this.agency = callRequest.getAgency().getId();
        this.text = callRequest.getText();
        this.createdAt = callRequest.getCreatedAt();
    }
}
