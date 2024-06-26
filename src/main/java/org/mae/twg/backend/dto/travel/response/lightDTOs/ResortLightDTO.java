package org.mae.twg.backend.dto.travel.response.lightDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Resort;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.ResortLocal;

@Data
@AllArgsConstructor
public class ResortLightDTO implements ModelDTO {
    private Long id;
    private String name;
    private Localization localization;

    public ResortLightDTO(Resort resort, Localization localization) {
        this.id = resort.getId();
        ResortLocal cur_local =
                resort.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Resort "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.localization = localization;
    }

    static public ResortLightDTO getDTO(Resort resort, Localization localization) {
        if (resort == null || resort.getIsDeleted()) {
            return null;
        }
        return new ResortLightDTO(resort, localization);
    }
}
