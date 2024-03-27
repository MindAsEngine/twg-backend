package org.mae.twg.backend.repositories.travel.comments;

import org.mae.twg.backend.models.travel.comments.TourComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TourCommentsRepo extends JpaRepository<TourComment, Long> {
    @Query("select c.tour.id as tourId, avg(c.grade) as avGrade " +
            "from TourComment c " +
            "where c.isDeleted = false " +
            "group by c.tour.id")
    Map<Long, Double> allAverageGrades();
    @Query("select avg(c.grade) as avGrade " +
            "from TourComment c " +
            "where c.isDeleted = false and c.tour.id = ?1")
    Double averageGradeByTourId(Long tourId);

    List<TourComment> findAllByTour_IdOrderByCreatedAtDesc(Long id);
    List<TourComment> findAllByTour_IdOrderByCreatedAtDesc(Long id, Pageable pageable);
}