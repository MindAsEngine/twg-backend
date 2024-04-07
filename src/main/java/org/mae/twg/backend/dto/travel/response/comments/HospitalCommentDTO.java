package org.mae.twg.backend.dto.travel.response.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.profile.UserLightDTO;
import org.mae.twg.backend.models.travel.comments.HospitalComment;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Log4j2
public class HospitalCommentDTO implements Serializable {
    private Long id;
    private UserLightDTO author;
    private String comment;
    private Integer grade;
    private LocalDateTime createdAt;

    public HospitalCommentDTO(HospitalComment comment) {
        log.debug("start HospitalCommentDTO constructor");
        this.id = comment.getId();
        this.author = new UserLightDTO(comment.getUser());
        this.comment = comment.getComment();
        this.grade = comment.getGrade();
        this.createdAt = comment.getCreatedAt();
        log.debug("end HospitalCommentDTO constructor");
    }

    static public HospitalCommentDTO getDTO(HospitalComment comment) {
        log.debug("start HospitalCommentDTO.getDTO");
        if (comment == null || comment.getIsDeleted()) {
            return null;
        }
        log.debug("end HospitalCommentDTO.getDTO");
        return new HospitalCommentDTO(comment);
    }
}
