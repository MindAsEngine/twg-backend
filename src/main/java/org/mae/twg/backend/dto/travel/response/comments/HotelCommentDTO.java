package org.mae.twg.backend.dto.travel.response.comments;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.mae.twg.backend.dto.profile.UserLightDTO;
import org.mae.twg.backend.models.travel.comments.HotelComment;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HotelCommentDTO implements Serializable {
    private Long id;
    private UserLightDTO author;
    private String comment;
    private Integer grade;
    private LocalDateTime createdAt;

    public HotelCommentDTO(HotelComment comment) {
        this.id = comment.getId();
        this.author = new UserLightDTO(comment.getUser());
        this.comment = comment.getComment();
        this.grade = comment.getGrade();
        this.createdAt = comment.getCreatedAt();
    }

    static public HotelCommentDTO getDTO(HotelComment comment) {
        if (comment == null || comment.getIsDeleted()) {
            return null;
        }
        return new HotelCommentDTO(comment);
    }
}
