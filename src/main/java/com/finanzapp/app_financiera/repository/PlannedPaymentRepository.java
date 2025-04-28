package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.PlannedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlannedPaymentRepository extends JpaRepository<PlannedPayment, Integer> {

    @Query("""
        SELECT p FROM PlannedPayment p
        WHERE p.userId = :userId
          AND p.paymentDate IS NULL
          AND (:dueDate IS NULL OR p.dueDate <= :dueDate)
          AND (
               :query IS NULL OR :query = ''
               OR LOWER(p.type) LIKE CONCAT('%', LOWER(:query), '%')
               OR LOWER(p.category) LIKE CONCAT('%', LOWER(:query), '%')
               OR LOWER(p.name) LIKE CONCAT('%', LOWER(:query), '%')
               OR CAST(p.amount AS string) LIKE CONCAT('%', :query, '%')
           )
        ORDER BY p.dueDate
        """)
    List<PlannedPayment> findByUserIdAndFilters(
        @Param("userId") int userId,
        @Param("dueDate") LocalDate dueDate,
        @Param("query") String query
    );
}



