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
    /* Consulta que obtiene todos los pagos planificados de un usuario
    - Solo selecciona los pagos que aún no han sido realizados (payment_date IS NULL)
    - Permite filtrar hasta una fecha límite de vencimiento (limitDate)
    - Permite buscar por coincidencias parciales en tipo, categoría, nombre, monto o fecha de vencimiento
    - Si no se envía texto de búsqueda (query vacío o null), ignora el filtro de búsqueda
    - La búsqueda es insensible a mayúsculas/minúsculas
    - Los resultados se ordenan por fecha de vencimiento (due_date)*/
    @Query(value = """
        SELECT *
          FROM planned_payments p
         WHERE p.user_id = :userId
           AND p.payment_date IS NULL
           AND (:limitDate IS NULL OR p.due_date <= :limitDate)
           AND (
                :query IS NULL OR :query = ''
                OR LOWER(p.type) LIKE CONCAT('%', LOWER(:query), '%')
                OR LOWER(p.category) LIKE CONCAT('%', LOWER(:query), '%')
                OR LOWER(p.name) LIKE CONCAT('%', LOWER(:query), '%')
                OR CAST(p.amount AS CHAR) LIKE CONCAT('%', :query, '%')
                OR DATE_FORMAT(p.due_date, '%Y-%m-%d') LIKE CONCAT('%', :query, '%')
           )
         ORDER BY p.due_date
        """,
        nativeQuery = true
    )
    List<PlannedPayment> findByUserIdAndFilters(
        @Param("userId") int userId,
        @Param("limitDate") LocalDate limitDate,
        @Param("query") String query
    );

}

