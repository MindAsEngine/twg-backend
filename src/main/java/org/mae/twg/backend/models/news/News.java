package org.mae.twg.backend.models.news;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.mae.twg.backend.models.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "news")
public class News implements Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "news_id")
    private Long id;
    @Column(name = "slug", unique = true)
    private String slug;
    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private Boolean isDeleted = Boolean.FALSE;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDate createdAt;
    @OneToMany(mappedBy = "news",
            cascade = CascadeType.ALL)
    private List<NewsLocal> locals = new ArrayList<>();
    @OneToMany(mappedBy = "news",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<NewsMedia> medias = new ArrayList<>();
    public void addLocal(NewsLocal local) {
        locals.add(local);
        local.setNews(this);
    }
    public void removeLocal(NewsLocal local) {
        locals.remove(local);
        local.setNews(null);
    }
    public void addMedia(NewsMedia media) {
        medias.add(media);
        media.setNews(this);
    }
    public void removeMedia(NewsMedia media) {
        medias.remove(media);
        media.setNews(null);
    }
    @Override
    public Long getId() {
        return id;
    }
    @Override
    public List<NewsLocal> getLocalizations() {
        return locals;
    }
}
