package org.mae.twg.backend.repositories.travel.comments;

import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.models.travel.comments.TourComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourCommentsRepo extends JpaRepository<TourComment, Long> {
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(c.tour.id, avg(c.grade), count(c.grade)) " +
            "from TourComment c " +
            "where c.isDeleted = false " +
            "group by c.tour.id")
    List<GradeData> allAverageGrades();
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(?1, avg(c.grade), count(c.grade)) " +
            "from TourComment c " +
            "where c.isDeleted = false and c.tour.id = ?1")
    GradeData averageGradeByTourId(Long tourId);

    List<TourComment> findAllByTour_IdOrderByCreatedAtDesc(Long id);
    List<TourComment> findAllByTour_IdOrderByCreatedAtDesc(Long id, Pageable pageable);
}
