package org.mae.twg.backend.repositories.travel.comments;

import org.mae.twg.backend.dto.GradeData;
import org.mae.twg.backend.models.travel.comments.HospitalComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalCommentsRepo extends JpaRepository<HospitalComment, Long> {
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(c.hospital.id, avg(c.grade), count(c.grade)) " +
            "from HospitalComment c " +
            "where c.isDeleted = false " +
            "group by c.hospital.id")
    List<GradeData> allAverageGrades();
    @Query("select " +
            "   new org.mae.twg.backend.dto.GradeData(?1, avg(c.grade), count(c.grade)) " +
            "from HospitalComment c " +
            "where c.isDeleted = false and c.hospital.id = ?1")
    GradeData averageGradeByHospitalId(Long hospitalId);

    List<HospitalComment> findAllByHospital_IdOrderByCreatedAtDesc(Long id);
    List<HospitalComment> findAllByHospital_IdOrderByCreatedAtDesc(Long id, Pageable pageable);
    Optional<HospitalComment> findByUser_IdAndHospital_Id(Long authorId, Long hotelId);
    Boolean existsByUser_IdAndHospital_Id(Long authorId, Long hotelId);
}
