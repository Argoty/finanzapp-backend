package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Integer> {

    @Query("""
        SELECT r FROM Record r
        WHERE r.userId = :userId
          AND (:dateLimit IS NULL OR r.date >= :dateLimit)
          AND (
               :query IS NULL OR :query = ''
               OR LOWER(r.type) LIKE CONCAT('%', LOWER(:query), '%')
               OR (r.description IS NOT NULL AND LOWER(r.description) LIKE CONCAT('%', LOWER(:query), '%'))
               OR LOWER(r.category) LIKE CONCAT('%', LOWER(:query), '%')
               OR CAST(r.amount AS string) LIKE CONCAT('%', :query, '%')
          )
        ORDER BY r.date DESC
        """)
    List<Record> findByUserIdAndFilters(
            @Param("userId") int userId,
            @Param("dateLimit") LocalDateTime dateLimit,
            @Param("query") String query
    );

    List<Record> findAllByUserIdAndTypeOrderByDateDesc(int userId, String type);

    List<Record> findAllByUserIdAndTypeAndDateGreaterThanEqualOrderByDateDesc(
            int userId, String type, LocalDateTime dateLimit);

    List<Record> findAllByUserIdAndTypeAndCategoryAndDateGreaterThanEqual(
            int userId,
            String type,
            String category,
            LocalDateTime date
    );
}
