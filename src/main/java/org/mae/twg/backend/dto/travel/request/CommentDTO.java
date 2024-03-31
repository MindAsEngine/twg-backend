package org.mae.twg.backend.dto.travel.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentDTO implements Serializable {
    private String comment;
    private Integer grade;
}
