package org.mae.twg.backend.utils.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourRow {
    @CsvBindByName(column = "RU title")
    private String titleRU;
    @CsvBindByName(column = "RU introduction")
    private String introductionRU;
    @CsvBindByName(column = "RU description")
    private String descriptionRU;
    @CsvBindByName(column = "RU additional")
    private String additionalRU;
    @CsvBindByName(column = "EN title")
    private String titleEN;
    @CsvBindByName(column = "EN introduction")
    private String introductionEN;
    @CsvBindByName(column = "EN description")
    private String descriptionEN;
    @CsvBindByName(column = "EN additional")
    private String additionalEN;
    @CsvBindByName(column = "UZ title")
    private String titleUZ;
    @CsvBindByName(column = "UZ introduction")
    private String introductionUZ;
    @CsvBindByName(column = "UZ description")
    private String descriptionUZ;
    @CsvBindByName(column = "UZ additional")
    private String additionalUZ;
    @CsvBindByName
    private Long price;
    @CsvBindByName(column = "type")
    private String tourType;
    @CsvBindByName
    private Integer duration;
}
