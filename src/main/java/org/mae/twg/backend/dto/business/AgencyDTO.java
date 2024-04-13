package org.mae.twg.backend.dto.business;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.dto.profile.UserLightDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.business.Agency;
import org.mae.twg.backend.models.business.AgencyLocal;
import org.mae.twg.backend.models.travel.enums.Localization;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@Log4j2
public class AgencyDTO implements Serializable, ModelDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String contacts;
    private List<UserLightDTO> agents;
    private Localization localization;


    public AgencyDTO(Agency agency, Localization localization) {
        log.debug("start AgencyDTO constructor");
        this.id = agency.getId();
        AgencyLocal cur_local =
                agency.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Agency "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.description = cur_local.getDescription();
        this.address = cur_local.getAddress();
        this.contacts = cur_local.getContacts();
        this.agents = agency.getAgents().stream().map(UserLightDTO::getDTO).filter(Objects::nonNull).toList();
        this.localization = localization;
        log.debug("end AgencyDTO constructor");
    }

    static public AgencyDTO getDTO(Agency agency, Localization localization) {
        log.debug("start AgencyDTO.getDTO");
        if (agency == null || agency.getIsDeleted()) {
            return null;
        }
        log.debug("end AgencyDTO.getDTO");
        return new AgencyDTO(agency, localization);
    }
}
