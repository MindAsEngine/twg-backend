package org.mae.twg.backend.repositories.travel.comments;

import org.mae.twg.backend.models.travel.comments.HotelComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface HotelCommentsRepo extends JpaRepository<HotelComment, Long> {
    @Query("select c.hotel.id as hotelId, avg(c.grade) as avGrade from HotelComment c group by c.hotel.id")
    Map<Long, Double> allAverageGrades();
    @Query("select avg(c.grade) as avGrade from HotelComment c where c.hotel.id = ?1")
    Double averageGradeByHotelId(Long hotelId);
}
