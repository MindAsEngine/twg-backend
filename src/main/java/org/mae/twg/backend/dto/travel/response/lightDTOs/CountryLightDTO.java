package org.mae.twg.backend.dto.travel.response.lightDTOs;

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
public class CountryLightDTO implements ModelDTO {
    private Long id;
    private String title;
    private Localization localization;

    public CountryLightDTO(Country country, Localization localization) {
        log.debug("start CountryLightDTO constructor");
        this.id = country.getId();
        CountryLocal cur_local =
                country.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Country '"
                                + localization.name() + "' localization not found"));
        this.title = cur_local.getName();
        this.localization = localization;
        log.debug("end CountryLightDTO constructor");
    }


    static public CountryLightDTO getDTO(Country country, Localization localization) {
        log.debug("start CountryLightDTO.getDTO");
        if (country == null || country.getIsDeleted()) {
            return null;
        }
        log.debug("end CountryLightDTO.getDTO");
        return new CountryLightDTO(country, localization);
    }
}
