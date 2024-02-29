package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.CountryLocal;

@Data
@AllArgsConstructor
public class CountryDTO implements ModelDTO {
    private Long id;
    private String title;
    private String description;
    private String media;
    private Localization localization;

    public CountryDTO(Country country, Localization localization) {
        this.id = country.getId();
        CountryLocal cur_local =
                country.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Country '"
                                + localization.name() + "' localization not found"));
        this.title = cur_local.getName();
        this.description = cur_local.getDescription();
        this.localization = localization;
        this.media = country.getMediaPath();
    }


    static public CountryDTO getDTO(Country country, Localization localization) {
        if (country == null || country.getIsDeleted()) {
            return null;
        }
        return new CountryDTO(country, localization);
    }
}
