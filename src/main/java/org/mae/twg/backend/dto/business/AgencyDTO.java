package org.mae.twg.backend.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.AgencyLocal;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AgencyDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String contacts;
    private Localization localization;

    public AgencyDTO(Agency agency, Localization localization) {
        this.id = agency.getId();
        AgencyLocal cur_local =
                agency.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Agency "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.description = cur_local.getDescription();
        this.contacts = cur_local.getContacts();
        this.localization = localization;
    }
}
