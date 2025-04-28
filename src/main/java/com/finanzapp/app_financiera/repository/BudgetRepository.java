package com.finanzapp.app_financiera.repository;

import com.finanzapp.app_financiera.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findAllByUserId(int userId);
    List<Budget> findAllByUserIdAndPeriod(int userId, String period);
}
