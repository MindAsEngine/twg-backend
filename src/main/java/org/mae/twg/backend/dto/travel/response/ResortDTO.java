package org.mae.twg.backend.dto.travel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.dto.travel.response.lightDTOs.CountryLightDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Resort;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.ResortLocal;

@Data
@AllArgsConstructor
@Log4j2
public class ResortDTO implements ModelDTO {
    private Long id;
    private String name;
    private CountryLightDTO country;
    private Localization localization;

    public ResortDTO(Resort resort, Localization localization) {
        log.debug("start ResortDTO constructor");
        this.id = resort.getId();
        ResortLocal cur_local =
                resort.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Resort "
                                + localization.name() + " localization not found"));
        this.country = CountryLightDTO.getDTO(resort.getCountry(), localization);
        this.name = cur_local.getName();
        this.localization = localization;
        log.debug("end ResortDTO constructor");
    }

    static public ResortDTO getDTO(Resort resort, Localization localization) {
        if (resort == null || resort.getIsDeleted()) {
            return null;
        }
        return new ResortDTO(resort, localization);
    }
}
