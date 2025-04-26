package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Integer> {
    
    List<Debt> findAllByUserId(int userId);

    void deleteById(int id);
}
