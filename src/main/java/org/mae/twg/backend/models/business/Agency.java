package org.mae.twg.backend.models.business;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mae.twg.backend.models.travel.Tour;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "agencies")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "agency_id")
    private Long id;
//    TODO: add field for map data

    @OneToMany(mappedBy = "agency",
            cascade = CascadeType.DETACH,
            orphanRemoval = true)
    private List<Tour> tours = new ArrayList<>();

    @OneToMany(mappedBy = "agency",
            cascade = CascadeType.ALL)
    private List<AgencyLocal> locals = new ArrayList<>();

    @OneToMany(mappedBy = "agency",
            cascade = CascadeType.DETACH)
    private List<Manager> managers = new ArrayList<>();

    public void addLocal(AgencyLocal local) {
        locals.add(local);
        local.setAgency(this);
    }

    public void removeLocal(AgencyLocal local) {
        locals.remove(local);
        local.setAgency(null);
    }
}
