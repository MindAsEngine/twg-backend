package org.mae.twg.backend.dto.travel.request.geo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на изменение геоданных тура")
public class TourGeoDTO implements RequestDTO {
    @Schema(description = "Геоданные", example = "JSON массив с координатами")
    private String geoData;
}
