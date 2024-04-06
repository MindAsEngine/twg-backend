package org.mae.twg.backend.dto.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.models.business.TourRequest;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Заявка на тур")
@Log4j2
public class TourReqResponseDTO {
    @Schema(description = "id заявки", example = "1")
    private Long id;
    @Schema(description = "Тур", example = "1")
    private TourDTO tour;
    @Schema(description = "Агентство", example = "1")
    private AgencyDTO agency;
    @Schema(description = "Количество взрослых", example = "1")
    private Integer adults;
    @Schema(description = "Количество детей", example = "1")
    private Integer children;
    @Schema(description = "Заметки", example = "Какие-то заметки")
    private String transferNotes;
    @Schema(description = "Время создания", example = "01.01.01")
    private LocalDateTime createdAt;
    @Schema(description = "Время закрытия ", example = "01.01.01")
    private LocalDateTime closedAt;
    public TourReqResponseDTO(TourRequest tourRequest, Localization localization) {
        log.debug("start TourReqResponseDTO constructor");
        this.id = tourRequest.getId();
        this.tour = TourDTO.getDTO(tourRequest.getTour(), localization);
        this.agency = AgencyDTO.getDTO(tourRequest.getAgency(), localization);
        this.adults = tourRequest.getAdults();
        this.children = tourRequest.getChildren();
        this.transferNotes = tourRequest.getTransferNotes();
        this.createdAt = tourRequest.getCreatedAt();
        this.closedAt = tourRequest.getClosedAt();
        log.debug("end TourReqResponseDTO constructor");
    }
}
