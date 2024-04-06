package org.mae.twg.backend.dto.travel.response.lightDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Resort;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.ResortLocal;

@Data
@AllArgsConstructor
@Log4j2
public class ResortLightDTO implements ModelDTO {
    private Long id;
    private String name;
    private Localization localization;

    public ResortLightDTO(Resort resort, Localization localization) {
        log.debug("start ResortLightDTO constructor");
        this.id = resort.getId();
        ResortLocal cur_local =
                resort.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Resort "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.localization = localization;
        log.debug("end ResortLightDTO constructor");
    }

    static public ResortLightDTO getDTO(Resort resort, Localization localization) {
        log.debug("start ResortLightDTO.getDTO");
        if (resort == null || resort.getIsDeleted()) {
            return null;
        }
        log.debug("end ResortLightDTO.getDTO");
        return new ResortLightDTO(resort, localization);
    }
}
