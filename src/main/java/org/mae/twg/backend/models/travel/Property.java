package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.travel.localization.PropertyLocal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "property_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "property",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<PropertyLocal> locals = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "properties")
    private Set<Hotel> hotels = new HashSet<>();

    public void addLocal(PropertyLocal local) {
        locals.add(local);
        local.setProperty(this);
    }

    public void removeLocal(PropertyLocal local) {
        locals.remove(local);
        local.setProperty(this);
    }
}
