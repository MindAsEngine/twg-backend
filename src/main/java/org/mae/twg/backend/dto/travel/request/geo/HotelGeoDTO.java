package org.mae.twg.backend.dto.travel.request.geo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на изменение геоданных отеля")
public class HotelGeoDTO implements RequestDTO {
    @Schema(description = "Широта", example = "33.0144")
    private Double latitude;
    @Schema(description = "Долгота", example = "35.4254")
    private Double longitude;
}
