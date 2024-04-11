package org.mae.twg.backend.utils.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class TourRow {
    @CsvBindByName
    private String titleRU;
    @CsvBindByName
    private String introductionRU;
    @CsvBindByName
    private String descriptionRU;
    @CsvBindByName
    private String additionalRU;
    @CsvBindByName
    private String titleEN;
    @CsvBindByName
    private String introductionEN;
    @CsvBindByName
    private String descriptionEN;
    @CsvBindByName
    private String additionalEN;
    @CsvBindByName
    private String titleUZ;
    @CsvBindByName
    private String introductionUZ;
    @CsvBindByName
    private String descriptionUZ;
    @CsvBindByName
    private String additionalUZ;
    @CsvBindByName
    private Long price;
    @CsvBindByName(column = "type", required = true)
    private String tourType;
    @CsvBindByName(column = "hospital name")
    private String hospitalName;
    @CsvBindByName(column = "country name")
    private String countryName;
    @CsvBindByName
    private Integer duration;
    @CsvBindByName(column = "hotel names")
    private String hotelList;
    @CsvBindByName(column = "tag names")
    private String tagList;
}
