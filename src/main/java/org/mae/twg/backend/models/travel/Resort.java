package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.localization.ResortLocal;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resorts")
public class Resort implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "resort_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToMany(mappedBy = "resort",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ResortLocal> locals = new ArrayList<>();

    @OneToMany(mappedBy = "resort",
            cascade = CascadeType.DETACH,
            orphanRemoval = true)
    private List<Hotel> hotels = new ArrayList<>();

    public void addLocal(ResortLocal local) {
        locals.add(local);
        local.setResort(this);
    }

    public void removeLocal(ResortLocal local) {
        locals.remove(local);
        local.setResort(null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<ResortLocal> getLocalizations() {
        return locals;
    }
}
