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
    List<Record> findByUserId(int userId);
    List<Record> findAllByUserIdAndType(int userId, String type);
    /* Método que busca registros (records) de un usuario con filtros opcionales:
     - Si se pasa `dateLimit`, solo trae registros desde esa fecha en adelante.
     - Si se pasa `query`, busca coincidencias parciales en varios campos: tipo, descripción, categoría, monto o fecha formateada.
     - La búsqueda es insensible a mayúsculas/minúsculas.
     - Si `query` está vacío o es null, no aplica filtro de búsqueda.
     - Ordena los resultados por fecha descendente (más recientes primero).*/
    @Query(value = """
        SELECT *
          FROM records r
         WHERE r.user_id = :userId
           AND (:dateLimit IS NULL OR r.date >= :dateLimit)
           AND (
                :query IS NULL OR :query = ''
                OR LOWER(r.type) LIKE CONCAT('%', LOWER(:query), '%')
                OR (r.description IS NOT NULL AND LOWER(r.description) LIKE CONCAT('%', LOWER(:query), '%'))
                OR LOWER(r.category) LIKE CONCAT('%', LOWER(:query), '%')
                OR CAST(r.amount AS CHAR) LIKE CONCAT('%', :query, '%')
                OR DATE_FORMAT(r.date, '%d/%m/%Y %H:%i') LIKE CONCAT('%', :query, '%')
           )
         ORDER BY r.date DESC
        """,
            nativeQuery = true
    )
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
