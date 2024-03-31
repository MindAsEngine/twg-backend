package org.mae.twg.backend.dto.travel.request.geo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на изменение геоданных тура")
public class TourGeoDTO implements RequestDTO {
    @Schema(description = "Геоданные", example = "[[20.00,30.00],[20.001,30.001]]")
    private String geoData;
}
