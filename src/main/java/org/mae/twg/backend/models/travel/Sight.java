package org.mae.twg.backend.models.travel;

import jakarta.persistence.*;
import lombok.*;
import org.mae.twg.backend.models.travel.localization.SightLocal;
import org.mae.twg.backend.models.travel.media.SightMedia;

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
@Table(name = "sights")
public class Sight implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sight_id")
    private Long id;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;

//    @Column(name = "slug", unique = true)
//    private String slug;
//    TODO: add slug generation

    @OneToMany(mappedBy = "sight",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SightLocal> locals = new ArrayList<>();
//    TODO: add field for map data

    @ManyToMany(fetch = FetchType.LAZY,
            mappedBy = "sights")
    private Set<Hotel> hotels = new HashSet<>();

    @OneToMany(mappedBy = "sight",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SightMedia> medias = new ArrayList<>();

    public void addLocal(SightLocal local) {
        locals.add(local);
        local.setSight(this);
    }

    public void removeLocal(SightLocal local) {
        locals.remove(local);
        local.setSight(null);
    }

    public void addMedia(SightMedia media) {
        medias.add(media);
        media.setSight(this);
    }

    public void removeMedia(SightMedia media) {
        medias.remove(media);
        media.setSight(null);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<SightLocal> getLocalizations() {
        return locals;
    }
}
