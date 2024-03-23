package org.mae.twg.backend.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Resort;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.ResortLocal;

@Data
@AllArgsConstructor
public class ResortDTO implements ModelDTO {
    private Long id;
    private String name;
    private Localization localization;

    public ResortDTO(Resort resort, Localization localization) {
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

    static public ResortDTO getDTO(Resort resort, Localization localization) {
        if (resort == null || resort.getIsDeleted()) {
            return null;
        }
        return new ResortDTO(resort, localization);
    }
}
