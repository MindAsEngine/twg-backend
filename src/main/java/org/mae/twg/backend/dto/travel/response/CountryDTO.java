package org.mae.twg.backend.dto.travel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.CountryLocal;

@Data
@AllArgsConstructor
@Log4j2
public class CountryDTO implements ModelDTO {
    private Long id;
    private String title;
    private String geoData;
    private String media;
    private Localization localization;

    public CountryDTO(Country country, Localization localization) {
        log.debug("start CountryDTO constructor");
        this.id = country.getId();
        CountryLocal cur_local =
                country.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Country '"
                                + localization.name() + "' localization not found"));
        this.title = cur_local.getName();
        this.geoData = country.getGeoData();
        this.localization = localization;
        this.media = country.getMediaPath();
        log.debug("end CountryDTO constructor");
    }


    static public CountryDTO getDTO(Country country, Localization localization) {
        log.debug("start CountryDTO.getDTO");
        if (country == null || country.getIsDeleted()) {
            return null;
        }
        log.debug("end CountryDTO.getDTO");
        return new CountryDTO(country, localization);
    }
}
