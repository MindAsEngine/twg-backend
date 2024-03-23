package org.mae.twg.backend.models.travel.comments;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.Sight;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sight_comments")
public class SightComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @NonNull
    private Integer grade;

    @Column(name = "comment",
            columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sight_id")
    private Sight sight;

    public SightComment(@NonNull User user,
                        @NonNull Integer grade,
                        String comment) {
        this.user = user;
        this.grade = grade;
        this.comment = comment;
    }
}
