package org.mae.twg.backend.dto.travel.request.geo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.mae.twg.backend.dto.RequestDTO;

@Data
@Schema(description = "Запрос на изменение геоданных страны")
public class CountryGeoDTO implements RequestDTO {
    @Schema(description = "Геоданные", example = "JSON массив с координатами")
    private String geoData;
}
