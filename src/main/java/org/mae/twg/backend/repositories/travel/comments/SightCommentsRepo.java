package org.mae.twg.backend.repositories.travel.comments;

import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.models.travel.comments.SightComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SightCommentsRepo extends JpaRepository<SightComment, Long> {
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(c.sight.id, avg(c.grade), count(c.grade)) " +
            "from SightComment c " +
            "where c.isDeleted = false " +
            "group by c.sight.id")
    List<GradeData> allAverageGrades();
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(c.sight.id, avg(c.grade), count(c.grade)) " +
            "from SightComment c " +
            "where c.isDeleted = false and c.sight.id = ?1")
    GradeData averageGradeBySightId(Long sightId);

    List<SightComment> findAllBySight_IdOrderByCreatedAtDesc(Long id);
    List<SightComment> findAllBySight_IdOrderByCreatedAtDesc(Long id, Pageable pageable);
}
