package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.mae.twg.backend.models.travel.localization.ResortLocal;
import org.mae.twg.backend.models.travel.media.ResortMedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resorts")
public class Resort {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "resort_id")
    private Long id;

//    @Column(name = "slug", unique = true)
//    private String slug;
//    TODO: add slug generation

    @OneToMany(mappedBy = "resort",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ResortLocal> locals = new ArrayList<>();

    @OneToMany(mappedBy = "resort",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ResortMedia> medias = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "resorts")
    private Set<Tour> tours = new HashSet<>();

    public void addLocal(ResortLocal local) {
        locals.add(local);
        local.setResort(this);
    }

    public void removeLocal(ResortLocal local) {
        locals.remove(local);
        local.setResort(null);
    }

    public void addMedia(ResortMedia media) {
        medias.add(media);
        media.setResort(this);
    }

    public void removeMedia(ResortMedia media) {
        medias.remove(media);
        media.setResort(null);
    }
}
