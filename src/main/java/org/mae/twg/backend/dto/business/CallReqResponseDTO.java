package org.mae.twg.backend.dto.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.profile.UserLightDTO;
import org.mae.twg.backend.models.business.CallRequest;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Заявка на звонок")
@Log4j2
public class CallReqResponseDTO {
    @Schema(description = "id заявки", example = "1")
    private Long id;
    @Schema(description = "Фио пользователя", example = "Романов Дмитрий Александрович")
    private String fullName;
    @Schema(description = "Телефон пользователя", example = "88005553535")
    private String phone;
    @Schema(description = "Агентство", example = "1")
    private AgencyDTO agency;
    @Schema(description = "Агент исполнитель")
    private UserLightDTO agent;
    @Schema(description = "Вопрос", example = "Как какать?")
    private String text;
    @Schema(description = "Дата создания", example = "01.01.01")
    private LocalDateTime createdAt;

    public CallReqResponseDTO(CallRequest callRequest, Localization localization) {
        log.debug("start CallReqResponseDTO constructor");
        this.id = callRequest.getId();
        this.fullName = callRequest.getUser();
        this.phone = callRequest.getNumber();
        this.agency = AgencyDTO.getDTO(callRequest.getAgency(), localization);
        this.agent = UserLightDTO.getDTO(callRequest.getAgent());
        this.text = callRequest.getText();
        this.createdAt = callRequest.getCreatedAt();
        log.debug("end CallReqResponseDTO constructor");
    }
}
