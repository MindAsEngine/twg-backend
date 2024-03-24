package org.mae.twg.backend.models.news;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.mae.twg.backend.models.Local;
import org.mae.twg.backend.models.Model;
import org.mae.twg.backend.models.travel.enums.Localization;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "news_local")
public class NewsLocal implements Local {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "local_id")
    private Long id;

    @NonNull
    @Column(name = "title")
    private String title;

    @Column(name = "description",
            columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Localization localization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    private News news;

    public NewsLocal(@NonNull String title,
                     String description,
                     News news,
                     Localization localization) {
        this.title = title;
        this.description = description;
        this.news = news;
        this.localization = localization;
    }

    @Override
    public String getString() {
        return title;
    }

    @Override
    public Model getModel() {
        return null;
    }
}
