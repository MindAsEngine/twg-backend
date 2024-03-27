package org.mae.twg.backend.repositories.travel.comments;

import org.mae.twg.backend.models.travel.comments.SightComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SightCommentsRepo extends JpaRepository<SightComment, Long> {
    @Query("select c.sight.id as sightId, avg(c.grade) as avGrade " +
            "from SightComment c " +
            "where c.isDeleted = false " +
            "group by c.sight.id")
    Map<Long, Double> allAverageGrades();
    @Query("select avg(c.grade) as avGrade " +
            "from SightComment c " +
            "where c.isDeleted = false and c.sight.id = ?1")
    Double averageGradeBySightId(Long sightId);

    List<SightComment> findAllBySight_IdOrderByCreatedAtDesc(Long id);
    List<SightComment> findAllBySight_IdOrderByCreatedAtDesc(Long id, Pageable pageable);
}