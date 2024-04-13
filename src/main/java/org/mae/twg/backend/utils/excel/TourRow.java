package org.mae.twg.backend.utils.excel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourRow {
    private String titleRU;
    private String introductionRU;
    private String descriptionRU;
    private String additionalRU;
    private String titleEN;
    private String introductionEN;
    private String descriptionEN;
    private String additionalEN;
    private String titleUZ;
    private String introductionUZ;
    private String descriptionUZ;
    private String additionalUZ;
    private Long price;
    private String tourType;
    private Integer duration;
}

