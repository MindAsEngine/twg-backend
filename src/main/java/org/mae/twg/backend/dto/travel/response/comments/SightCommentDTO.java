package org.mae.twg.backend.dto.travel.response.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.auth.UserLightDTO;
import org.mae.twg.backend.models.travel.comments.SightComment;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SightCommentDTO implements Serializable {
    private Long id;
    private UserLightDTO author;
    private String comment;
    private Integer grade;
    private LocalDateTime createdAt;

    public SightCommentDTO(SightComment comment) {
        this.id = comment.getId();
        this.author = new UserLightDTO(comment.getUser());
        this.comment = comment.getComment();
        this.grade = comment.getGrade();
        this.createdAt = comment.getCreatedAt();
    }

    static public SightCommentDTO getDTO(SightComment comment) {
        if (comment == null || comment.getIsDeleted()) {
            return null;
        }
        return new SightCommentDTO(comment);
    }
}
