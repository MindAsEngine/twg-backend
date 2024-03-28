package org.mae.twg.backend.repositories.travel.comments;

import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.models.travel.comments.HotelComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelCommentsRepo extends JpaRepository<HotelComment, Long> {
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(c.hotel.id, avg(c.grade), count(c.grade)) " +
            "from HotelComment c " +
            "where c.isDeleted = false " +
            "group by c.hotel.id")
    List<GradeData> allAverageGrades();
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(?1, avg(c.grade), count(c.grade)) " +
            "from HotelComment c " +
            "where c.isDeleted = false and c.hotel.id = ?1")
    GradeData averageGradeByHotelId(Long hotelId);

    List<HotelComment> findAllByHotel_IdOrderByCreatedAtDesc(Long id);
    List<HotelComment> findAllByHotel_IdOrderByCreatedAtDesc(Long id, Pageable pageable);
    Optional<HotelComment> findByUser_IdAndHotel_Id(Long authorId, Long hotelId);
    Boolean existsByUser_IdAndHotel_Id(Long authorId, Long hotelId);
}
