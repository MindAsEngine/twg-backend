package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.localization.TagLocal;

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
@Table(name = "tag")
public class Tag implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "icon")
    private String icon;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

    @OneToMany(mappedBy = "tag",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TagLocal> locals = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "tags")
    private Set<Tour> tours = new HashSet<>();

    public void addLocal(TagLocal local) {
        locals.add(local);
        local.setTag(this);
    }

    public void removeLocal(TagLocal local) {
        locals.remove(local);
        local.setTag(null);
    }

    @Override
    public List<TagLocal> getLocalizations() {
        return locals;
    }
}
