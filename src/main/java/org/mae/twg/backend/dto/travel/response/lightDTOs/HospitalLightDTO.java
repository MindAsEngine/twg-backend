package org.mae.twg.backend.dto.travel.response.lightDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Hospital;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HospitalLocal;

@Data
@AllArgsConstructor
@Log4j2
public class HospitalLightDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String name;
    private Double latitude;
    private Double longitude;
    private Localization localization;

    public HospitalLightDTO(Hospital hospital, Localization localization) {
        log.debug("start HospitalLightDTO constructor");
        this.id = hospital.getId();
        this.slug = hospital.getSlug();
        HospitalLocal cur_local =
                hospital.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Hospital "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.latitude = hospital.getLatitude();
        this.longitude = hospital.getLongitude();
        this.localization = localization;
        log.debug("end HospitalLightDTO constructor");
    }

    static public HospitalLightDTO getDTO(Hospital hospital, Localization localization) {
        log.debug("start HospitalLightDTO.getDTO");
        if (hospital == null || hospital.getIsDeleted()) {
            return null;
        }
        log.debug("end HospitalLightDTO.getDTO");
        return new HospitalLightDTO(hospital, localization);
    }
}
