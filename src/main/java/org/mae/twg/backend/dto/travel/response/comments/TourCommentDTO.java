package org.mae.twg.backend.dto.travel.response.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.profile.UserLightDTO;
import org.mae.twg.backend.models.travel.comments.TourComment;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Log4j2
public class TourCommentDTO implements Serializable {
    private Long id;
    private UserLightDTO author;
    private String comment;
    private Integer grade;
    private LocalDateTime createdAt;

    public TourCommentDTO(TourComment comment) {
        log.debug("start TourComment constructor");
        this.id = comment.getId();
        this.author = new UserLightDTO(comment.getUser());
        this.comment = comment.getComment();
        this.grade = comment.getGrade();
        this.createdAt = comment.getCreatedAt();
        log.debug("end TourComment constructor");
    }

    static public TourCommentDTO getDTO(TourComment comment) {
        log.debug("start TourComment.getDTO");
        if (comment == null || comment.getIsDeleted()) {
            return null;
        }
        log.debug("end TourComment.getDTO");
        return new TourCommentDTO(comment);
    }
}
