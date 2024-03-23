package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.localization.SightTypeLocal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sight_types")
public class SightType implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "sightType",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SightTypeLocal> locals = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.DETACH,
            mappedBy = "sightType")
    private Set<Sight> sights = new HashSet<>();

    @Override
    public List<SightTypeLocal> getLocalizations() {
        return locals;
    }
}
