package org.mae.twg.backend.models.business;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "agencies")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "agency_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "agency",
            cascade = CascadeType.ALL)
    private List<AgencyLocal> locals = new ArrayList<>();

    public void addLocal(AgencyLocal local) {
        locals.add(local);
        local.setAgency(this);
    }

    public void removeLocal(AgencyLocal local) {
        locals.remove(local);
        local.setAgency(null);
    }
}
