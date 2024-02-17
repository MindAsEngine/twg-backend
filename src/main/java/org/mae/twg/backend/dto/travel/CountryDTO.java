package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.CountryLocal;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CountryDTO implements Serializable {
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
        this.title = cur_local.getTitle();
        this.description = cur_local.getDescription();
        this.localization = localization;
        this.media = country.getMediaPath();
    }
}
