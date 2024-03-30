package org.mae.twg.backend.dto.business;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.business.CallRequest;
import org.mae.twg.backend.models.business.TourRequest;
import org.mae.twg.backend.models.travel.Tour;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на тур")
public class TourRequestDTO {
    @Schema(description = "id тура", example = "1")
    private Long tour;
    @Schema(description = "Количество взрослых", example = "1")
    private Integer adults;
    @Schema(description = "Количество детей", example = "1")
    private Integer children;
    @Schema(description = "Заметки", example = "Какие-то заметки")
    private String transferNotes;
}
