package org.mae.twg.backend.dto.travel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.ModelDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.travel.Hospital;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.models.travel.localization.HospitalLocal;
import org.mae.twg.backend.models.travel.media.HospitalMedia;

import java.util.List;

@Data
@AllArgsConstructor
@Log4j2
public class HospitalDTO implements ModelDTO {
    private Long id;
    private String slug;
    private String name;
    private String city;
    private String introduction;
    private String description;
    private String address;
    private Double grade;
    private Double latitude;
    private Double longitude;
    private Long commentAmount;
    private Localization localization;
    private String header;
    private List<String> medias;

    public HospitalDTO(Hospital hospital, Localization localization) {
        log.debug("start HospitalDTO constructor");
        this.id = hospital.getId();
        this.slug = hospital.getSlug();
        HospitalLocal cur_local =
                hospital.getLocals().stream()
                        .filter(local -> local.getLocalization() == localization)
                        .findFirst()
                        .orElseThrow(() -> new ObjectNotFoundException("Hotel "
                                + localization.name() + " localization not found"));
        this.name = cur_local.getName();
        this.city = cur_local.getCity();
        this.introduction = cur_local.getIntroduction();
        this.description = cur_local.getDescription();
        this.address = cur_local.getAddress();
        this.latitude = hospital.getLatitude();
        this.longitude = hospital.getLongitude();
        this.header = (hospital.getHeader() != null ? hospital.getHeader().getMediaPath() : null);
        this.medias = hospital.getMedias().stream().map(HospitalMedia::getMediaPath).toList();
        this.localization = localization;
        log.debug("end HospitalDTO constructor");
    }

    static public HospitalDTO getDTO(Hospital hospital, Localization localization) {
        log.debug("start HospitalDTO.getDTO");
        if (hospital == null || hospital.getIsDeleted()) {
            return null;
        }
        log.debug("end HospitalDTO.getDTO");
        return new HospitalDTO(hospital, localization);
    }
}
