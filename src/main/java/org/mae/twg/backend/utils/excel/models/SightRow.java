package org.mae.twg.backend.utils.excel.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SightRow {
    private String nameRU;
    private String introductionRU;
    private String descriptionRU;
    private String addressRU;

    private String nameEN;
    private String introductionEN;
    private String descriptionEN;
    private String addressEN;

    private String nameUZ;
    private String introductionUZ;
    private String descriptionUZ;
    private String addressUZ;
    
    private Double latitude;
    private Double longitude;
}